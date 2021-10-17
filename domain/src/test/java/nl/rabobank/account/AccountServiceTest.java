package nl.rabobank.account;


import nl.rabobank.account.dto.AccountDto;
import nl.rabobank.account.exceptions.AccountAlreadyExistsException;
import nl.rabobank.account.exceptions.AccountNotFoundException;
import nl.rabobank.account.exceptions.InvalidAccountGrantorException;
import nl.rabobank.account.exceptions.PowerOfAttorneyAlreadyExistsException;
import nl.rabobank.account.interfaces.AccountRepository;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.authorizations.dto.PowerOfAttorneyDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountFactory accountFactory;

    @InjectMocks
    private AccountService accountService;

    @Captor
    ArgumentCaptor<Account> captor;

    @Test
    void shouldCreateAccount() {

        //Given
        final var accountHolderName = "John Lennon";
        final var accountNumber = "1234-567-89";
        final var type = AccountType.PAYMENT;
        final var account = new PaymentAccount(accountNumber, accountHolderName);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());
        when(accountFactory.create(accountNumber, accountHolderName, type)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);

        //When
        final var result = accountService.createAccount(accountNumber, accountHolderName, type);

        //Then
        final var expectedAccountDto = AccountDto.builder()
                .accountNumber(accountNumber)
                .accountHolderName(accountHolderName)
                .balance(0d)
                .build();
        assertThat(result).isEqualTo(expectedAccountDto);
    }

    @Test
    void shouldThrowAccountAlreadyExistsExceptionWhenCreateAndAccountExists() {

        //Given
        final var accountHolderName = "John Lennon";
        final var accountNumber = "1234-567-89";
        final var type = AccountType.PAYMENT;
        final var account = new PaymentAccount(accountNumber, accountHolderName);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        //When, Then
        assertThrows(AccountAlreadyExistsException.class,
                () -> accountService.createAccount(accountNumber, accountHolderName, type));
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(accountFactory);
    }

    @Test
    void shouldGrantPowerOfAttorneyWhenAccountPresentAndGrantorIsHolder() {

        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var account = new PaymentAccount(accountNumber, grantorName);

        final var granteeName = "Ringo Star";
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        //When
        final var result = accountService.grantPowerOfAttorney(grantorName, granteeName, accountNumber, Authorization.WRITE);

        //Then
        final var expectedPowerOfAttorneyDto = PowerOfAttorneyDto.builder()
                .grantorName(grantorName)
                .granteeName(granteeName)
                .accountNumber(accountNumber)
                .authorization("WRITE")
                .build();
        assertThat(result).isEqualTo(expectedPowerOfAttorneyDto);

        final var expectedPowerOfAttorney = PowerOfAttorney.builder()
                .grantorName(grantorName)
                .granteeName(granteeName)
                .account(account)
                .authorization(Authorization.WRITE)
                .build();

        final var expectedAccount = new PaymentAccount(accountNumber, grantorName);
        expectedAccount.getGrants().add(expectedPowerOfAttorney);

        verify(accountRepository).save(captor.capture());
        final var captorValue = captor.getValue();
        assertThat(captorValue).isEqualTo(account);
        assertThat(captorValue.getGrants()).containsExactlyInAnyOrder(expectedPowerOfAttorney);
    }

    @Test
    void shouldThrowPowerOfAttorneyAlreadyExistsExceptionWhenGrantAndPowerOfAttorneyAlreadyGranted() {

        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var account = new PaymentAccount(accountNumber, grantorName);

        final var powerOfAttorney = PowerOfAttorney.builder()
                .grantorName(grantorName)
                .granteeName(granteeName)
                .account(account)
                .authorization(Authorization.WRITE)
                .build();
        account.getGrants().add(powerOfAttorney);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        //When, Then
        assertThrows(PowerOfAttorneyAlreadyExistsException.class,
                () -> accountService.grantPowerOfAttorney(grantorName, granteeName, accountNumber, Authorization.WRITE));
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void shouldThrowAccountNotFoundExceptionWhenGrantAndAccountIsNotFound() {

        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        //When, Then
        assertThrows(AccountNotFoundException.class,
                () -> accountService.grantPowerOfAttorney(grantorName, granteeName, accountNumber, Authorization.WRITE));
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void shouldThrowInvalidAccountGrantorExceptionWhenGrantAndGrantorIsNotHolder() {

        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var account = new PaymentAccount(accountNumber, "Paul McCartney");

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        //When, Then
        assertThrows(InvalidAccountGrantorException.class,
                () -> accountService.grantPowerOfAttorney(grantorName, granteeName, accountNumber, Authorization.WRITE));
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void shouldRevokePowerOfAttorneyWhenRevokeAndAccountPresentAndGrantorIsHolder() {

        //Given
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var accountNumber = "1234-567-89";
        final var authorization = Authorization.WRITE;
        final var account = new PaymentAccount(accountNumber, grantorName);

        final var toDeletePowerOfAttorney1 = PowerOfAttorney.builder()
                .grantorName(grantorName)
                .granteeName(granteeName)
                .account(account)
                .authorization(Authorization.WRITE)
                .build();
        account.getGrants().add(toDeletePowerOfAttorney1);

        final var powerOfAttorney = PowerOfAttorney.builder()
                .grantorName(grantorName)
                .granteeName(granteeName)
                .account(account)
                .authorization(Authorization.READ)
                .build();
        account.getGrants().add(powerOfAttorney);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        //When
        accountService.revokePowerOfAttorney(grantorName, granteeName, accountNumber, authorization);

        //Then
        verify(accountRepository).save(captor.capture());
        final var captorValue = captor.getValue();
        assertThat(captorValue).isEqualTo(account);
        assertThat(captorValue.getGrants()).containsExactlyInAnyOrder(powerOfAttorney);
    }

    @Test
    void shouldNotChangeAccountWhenRevokeAndPowerOfAttorneyDoesNotExists() {

        //Given
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var accountNumber = "1234-567-89";
        final var authorization = Authorization.WRITE;
        final var account = new PaymentAccount(accountNumber, grantorName);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        //When
        accountService.revokePowerOfAttorney(grantorName, granteeName, accountNumber, authorization);

        //Then
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void shouldThrowAccountNotFoundExceptionWhenRevokeAndAccountIsNotFound() {

        //Given
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var authorization = Authorization.WRITE;
        when(accountRepository.findByAccountNumber("1234-567-81")).thenReturn(Optional.empty());

        //When, Then
        assertThrows(AccountNotFoundException.class,
                () -> accountService.revokePowerOfAttorney(grantorName, granteeName, "1234-567-81", authorization));
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void shouldInvalidAccountGrantorExceptionWhenRevokeAndGrantorIsNotHolder() {

        //Given
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var accountNumber = "1234-567-89";
        final var authorization = Authorization.WRITE;
        final var account = new PaymentAccount(accountNumber, grantorName);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        //When, Then
        assertThrows(InvalidAccountGrantorException.class,
                () -> accountService.revokePowerOfAttorney("Paul McCartney", granteeName, accountNumber, authorization));
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void shouldReturnAllAvailableAccounts() {

        //Given
        final var name = "John Lennon";

        final var ownAccount = new PaymentAccount("1234-567-89", "John Lennon", 9.99);
        final var grantedAccount = new SavingsAccount("1234-567-81", "Ringo Star", 9.99);

        when(accountRepository.findAllAvailableAccountsByClientsName(name)).thenReturn(List.of(ownAccount, grantedAccount));

        //When
        final var result = accountService.getAvailableAccounts(name);
        final var accountDto1 = AccountDto.builder()
                .accountHolderName("John Lennon")
                .accountNumber("1234-567-89")
                .balance(9.99)
                .build();
        final var accountDto2 = AccountDto.builder()
                .accountHolderName("Ringo Star")
                .accountNumber("1234-567-81")
                .balance(9.99)
                .build();
        final var expectedAccountDtoList = List.of(accountDto1, accountDto2);

        //Then
        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedAccountDtoList);
    }
}