package nl.rabobank.account;


import nl.rabobank.account.dto.AccountDto;
import nl.rabobank.account.exceptions.AccountAlreadyExistsException;
import nl.rabobank.account.interfaces.AccountRepository;
import nl.rabobank.authorizations.interfaces.PowerOfAttorneyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private PowerOfAttorneyRepository powerOfAttorneyRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountFactory accountFactory;

    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldCreateAccount() {

        //Given
        final var accountHolderName = "John Lennon";
        final var accountNumber = "1234-567-89";
        final var type = AccountType.PAYMENT;
        final var account = new PaymentAccount(accountNumber, accountHolderName, 0d);

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
    void shouldThrowAccountAlreadyExistsExceptionWhenAccountExists() {

        //Given
        final var accountHolderName = "John Lennon";
        final var accountNumber = "1234-567-89";
        final var type = AccountType.PAYMENT;
        final var account = new PaymentAccount(accountNumber, accountHolderName, 0d);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        //When, Then
        assertThrows(AccountAlreadyExistsException.class,
                () -> {
                    accountService.createAccount(accountNumber, accountHolderName, type);
                });
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(accountFactory);
    }

    @Test
    void shouldReturnAllAvailableAccounts() {

        //Given
        final var name = "John Lennon";

        final var ownAccount = new PaymentAccount("1234-567-89", "John Lennon", 9.99);
        final var grantedAccount = new SavingsAccount("1234-567-81", "Ringo Star", 9.99);

        when(accountRepository.findAllByAccountHolderName(name)).thenReturn(List.of(ownAccount));
        when(powerOfAttorneyRepository.findAllAccountsByGranteeName(name)).thenReturn(List.of(grantedAccount));

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