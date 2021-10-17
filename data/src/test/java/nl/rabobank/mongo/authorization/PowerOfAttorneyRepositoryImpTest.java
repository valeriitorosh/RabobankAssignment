package nl.rabobank.mongo.authorization;

import nl.rabobank.account.PaymentAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.mongo.account.AccountInternalRepository;
import nl.rabobank.mongo.account.AccountMapper;
import nl.rabobank.mongo.account.AccountRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PowerOfAttorneyRepositoryImpTest {

    @Mock
    private PowerOfAttorneyInternalRepository powerOfAttorneyInternalRepository;

    @Mock
    private AccountInternalRepository accountInternalRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private PowerOfAttorneyRepositoryImp powerOfAttorneyRepositoryImp;

    @Captor
    ArgumentCaptor<PowerOfAttorneyRecord> captor;

    @Test
    void shouldSavePowerOfAttorney() {

        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";

        final var account = new PaymentAccount(accountNumber, "John Lennon", 9.99);

        final var powerOfAttorney = PowerOfAttorney.builder()
                .granteeName(granteeName)
                .grantorName(grantorName)
                .account(account)
                .authorization(Authorization.WRITE)
                .build();

        //When
        powerOfAttorneyRepositoryImp.save(powerOfAttorney);

        //Then
        verify(powerOfAttorneyInternalRepository).save(captor.capture());
        final var captorValue = captor.getValue();
        assertThat(captorValue.getGranteeName()).isEqualTo(granteeName);
        assertThat(captorValue.getGrantorName()).isEqualTo(grantorName);
        assertThat(captorValue.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(captorValue.getAuthorization()).isEqualTo("WRITE");
    }

    @Test
    void shouldDeletePowerOfAttorney() {

        //Given
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var accountNumber = "1234-567-89";
        final var authorization = Authorization.READ;

        //When
        powerOfAttorneyRepositoryImp.delete(grantorName, granteeName, accountNumber, authorization);

        //Then
        verify(powerOfAttorneyInternalRepository).deleteByGrantorNameAndGranteeNameAndAccountNumberAndAuthorization(
                grantorName, granteeName, accountNumber, authorization.name());
    }

    @Test
    void shouldFindAllOwnOrGrantedAccounts() {

        //Given
        final var name = "John Lennon";

        final var grantedAccountRecord = AccountRecord.builder()
                .accountNumber("1234-567-81")
                .accountHolderName("Ringo Star")
                .type("PAYMENT")
                .balance(9.99)
                .build();
        final var grantedAccount = new PaymentAccount("1234-567-81", "Ringo Star", 9.99);
        final var powerOfAttorneyRecord = PowerOfAttorneyRecord.builder()
                .granteeName("John Lennon")
                .grantorName("Ringo Star")
                .accountNumber(grantedAccount.getAccountNumber())
                .authorization("WRITE")
                .build();

        when(powerOfAttorneyInternalRepository.findAllByGranteeName(name)).thenReturn(List.of(powerOfAttorneyRecord));
        when(accountMapper.mapToAccount(grantedAccountRecord)).thenReturn(grantedAccount);
        when(accountInternalRepository.findAllById(Set.of("1234-567-81"))).thenReturn(List.of(grantedAccountRecord));

        //When
        final var result = powerOfAttorneyRepositoryImp.findAllAccountsByGranteeName(name);

        //Then
        assertThat(result).containsExactlyInAnyOrderElementsOf(List.of(grantedAccount));
    }
}