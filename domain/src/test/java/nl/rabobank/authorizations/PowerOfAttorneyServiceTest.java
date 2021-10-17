package nl.rabobank.authorizations;

import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.interfaces.AccountRepository;
import nl.rabobank.authorizations.dto.PowerOfAttorneyDto;
import nl.rabobank.authorizations.exceptions.AccountNotFoundException;
import nl.rabobank.authorizations.exceptions.InvalidAccountGrantorException;
import nl.rabobank.authorizations.interfaces.PowerOfAttorneyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PowerOfAttorneyServiceTest {

    @Mock
    private PowerOfAttorneyRepository powerOfAttorneyRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private PowerOfAttorneyService powerOfAttorneyService;

    @Captor
    ArgumentCaptor<PowerOfAttorney> captor;

    @Test
    void shouldCreatePowerOfAttorneyWhenAccountPresentAndGrantorIsHolder() {

        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var account = new PaymentAccount(accountNumber, grantorName, 9.99);

        final var granteeName = "Ringo Star";
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        //When
        final var result = powerOfAttorneyService.createPowerOfAttorney(grantorName, granteeName, accountNumber, Authorization.WRITE);

        //Then
        final var expectedPowerOfAttorneyDto = PowerOfAttorneyDto.builder()
                .grantorName(grantorName)
                .granteeName(granteeName)
                .accountNumber(accountNumber)
                .authorization("WRITE")
                .build();
        assertThat(result).isEqualTo(expectedPowerOfAttorneyDto);

        verify(powerOfAttorneyRepository).save(captor.capture());
        final var captorValue = captor.getValue();
        assertThat(captorValue.getGranteeName()).isEqualTo(granteeName);
        assertThat(captorValue.getGrantorName()).isEqualTo(grantorName);
        assertThat(captorValue.getAccount()).isEqualTo(account);
        assertThat(captorValue.getAuthorization()).isEqualTo(Authorization.WRITE);
    }

    @Test
    void shouldThrowAccountNotFoundExceptionWhenCreateAccountIsNotFound() {

        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        //When, Then
        assertThrows(AccountNotFoundException.class,
                () -> {
                    powerOfAttorneyService.createPowerOfAttorney(grantorName, granteeName, accountNumber, Authorization.WRITE);
                });
        verifyNoInteractions(powerOfAttorneyRepository);
    }

    @Test
    void shouldInvalidAccountGrantorExceptionWhenCreateAndGrantorIsNotHolder() {

        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var account = new PaymentAccount(accountNumber, "Paul McCartney", 9.99);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        //When, Then
        assertThrows(InvalidAccountGrantorException.class,
                () -> {
                    powerOfAttorneyService.createPowerOfAttorney(grantorName, granteeName, accountNumber, Authorization.WRITE);
                });
        verifyNoInteractions(powerOfAttorneyRepository);
    }

    @Test
    void shouldDeletePowerOfAttorneyWhenAccountPresentAndGrantorIsHolder() {

        //Given
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var accountNumber = "1234-567-89";
        final var authorization = Authorization.WRITE;
        final var account = new PaymentAccount(accountNumber, grantorName, 9.99);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        //When
        powerOfAttorneyService.deletePowerOfAttorney(grantorName, granteeName, accountNumber, authorization);

        //Then
        verify(powerOfAttorneyRepository).delete(grantorName, granteeName, accountNumber, authorization);
    }

    @Test
    void shouldThrowAccountNotFoundExceptionWhenDeleteAndAccountIsNotFound() {

        //Given
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var accountNumber = "1234-567-89";
        final var authorization = Authorization.WRITE;
        when(accountRepository.findByAccountNumber("1234-567-81")).thenReturn(Optional.empty());

        //When, Then
        assertThrows(AccountNotFoundException.class,
                () -> {
                    powerOfAttorneyService.deletePowerOfAttorney(grantorName, granteeName, "1234-567-81", authorization);
                });
        verifyNoInteractions(powerOfAttorneyRepository);
    }

    @Test
    void shouldInvalidAccountGrantorExceptionWhenDeleteAndGrantorIsNotHolder() {

        //Given
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var accountNumber = "1234-567-89";
        final var authorization = Authorization.WRITE;
        final var account = new PaymentAccount(accountNumber, grantorName, 9.99);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        //When, Then
        assertThrows(InvalidAccountGrantorException.class,
                () -> {
                    powerOfAttorneyService.deletePowerOfAttorney("Paul McCartney", granteeName, accountNumber, authorization);
                });
        verifyNoInteractions(powerOfAttorneyRepository);
    }
}