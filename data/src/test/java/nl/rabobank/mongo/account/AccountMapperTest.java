package nl.rabobank.mongo.account;

import nl.rabobank.account.AccountFactory;
import nl.rabobank.account.AccountType;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.mongo.authorization.PowerOfAttorneyRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountMapperTest {

    @Mock
    private AccountFactory accountFactory;

    @InjectMocks
    private AccountMapper accountMapper;

    @Test
    void shouldMapAccountRecordToSavingsAccount() {

        //Given
        final var accountNumber = "1234-567-89";
        final var accountHolderName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var balance = 9.99;

        final var powerOfAttorneyRecord = PowerOfAttorneyRecord.builder()
                .granteeName(granteeName)
                .authorization("WRITE")
                .build();
        final var accountRecord = AccountRecord.builder()
                .type("SAVINGS")
                .accountNumber(accountNumber)
                .accountHolderName(accountHolderName)
                .balance(balance)
                .grants(Set.of(powerOfAttorneyRecord))
                .build();

        final var account = new SavingsAccount(accountNumber, accountHolderName, balance);
        when(accountFactory.create(accountNumber, accountHolderName, AccountType.SAVINGS, balance)).thenReturn(account);

        //When
        final var result = accountMapper.mapToAccount(accountRecord);

        //Then
        final var expectedSavingsAccount = new SavingsAccount(accountNumber, accountHolderName, balance);
        final var expectedPowerOfAttorney = PowerOfAttorney.builder()
                .account(expectedSavingsAccount)
                .grantorName(accountHolderName)
                .granteeName(granteeName)
                .authorization(Authorization.WRITE)
                .build();
        expectedSavingsAccount.getGrants().add(expectedPowerOfAttorney);
        assertThat(result).isEqualTo(expectedSavingsAccount);
    }

    @Test
    void shouldMapAccountRecordToPaymentsAccount() {

        //Given
        final var accountNumber = "1234-567-89";
        final var accountHolderName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var balance = 9.99;

        final var powerOfAttorneyRecord = PowerOfAttorneyRecord.builder()
                .granteeName(granteeName)
                .authorization("WRITE")
                .build();
        final var accountRecord = AccountRecord.builder()
                .type("PAYMENT")
                .accountNumber(accountNumber)
                .accountHolderName(accountHolderName)
                .balance(balance)
                .grants(Set.of(powerOfAttorneyRecord))
                .build();

        final var account = new PaymentAccount(accountNumber, accountHolderName, balance);
        when(accountFactory.create(accountNumber, accountHolderName, AccountType.PAYMENT, balance)).thenReturn(account);

        //When
        final var result = accountMapper.mapToAccount(accountRecord);

        //Then
        final var expectedPaymentAccount = new PaymentAccount(accountNumber, accountHolderName, balance);
        final var expectedPowerOfAttorney = PowerOfAttorney.builder()
                .account(expectedPaymentAccount)
                .grantorName(accountHolderName)
                .granteeName(granteeName)
                .authorization(Authorization.WRITE)
                .build();
        expectedPaymentAccount.getGrants().add(expectedPowerOfAttorney);
        assertThat(result).isEqualTo(expectedPaymentAccount);
    }

    @Test
    void shouldMapSavingsAccountToAccountRecord() {

        //Given
        final var accountNumber = "1234-567-89";
        final var accountHolderName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var balance = 9.99;
        final var savingsAccount = new SavingsAccount(accountNumber, accountHolderName, balance);
        final var powerOfAttorney = PowerOfAttorney.builder()
                .account(savingsAccount)
                .grantorName(accountHolderName)
                .granteeName(granteeName)
                .authorization(Authorization.WRITE)
                .build();
        savingsAccount.getGrants().add(powerOfAttorney);

        //When
        final var accountRecord = accountMapper.mapToAccountRecord(savingsAccount);

        //Then
        final var expectedPowerOfAttorneyRecord = PowerOfAttorneyRecord.builder()
                .granteeName(granteeName)
                .authorization("WRITE")
                .build();
        final var expectedAccountRecord = AccountRecord.builder()
                .type("SAVINGS")
                .accountNumber(accountNumber)
                .accountHolderName(accountHolderName)
                .balance(balance)
                .grants(Set.of(expectedPowerOfAttorneyRecord))
                .build();
        assertThat(accountRecord).isEqualTo(expectedAccountRecord);
    }

    @Test
    void shouldMapPaymentAccountToAccountRecord() {

        //Given
        final var accountNumber = "1234-567-89";
        final var accountHolderName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var balance = 9.99;
        final var paymentAccount = new PaymentAccount(accountNumber, accountHolderName, balance);
        final var powerOfAttorney = PowerOfAttorney.builder()
                .account(paymentAccount)
                .grantorName(accountHolderName)
                .granteeName(granteeName)
                .authorization(Authorization.WRITE)
                .build();
        paymentAccount.getGrants().add(powerOfAttorney);

        //When
        final var accountRecord = accountMapper.mapToAccountRecord(paymentAccount);

        //Then
        final var expectedPowerOfAttorneyRecord = PowerOfAttorneyRecord.builder()
                .granteeName(granteeName)
                .authorization("WRITE")
                .build();
        final var expectedAccountRecord = AccountRecord.builder()
                .type("PAYMENT")
                .accountNumber(accountNumber)
                .accountHolderName(accountHolderName)
                .balance(balance)
                .grants(Set.of(expectedPowerOfAttorneyRecord))
                .build();
        assertThat(accountRecord).isEqualTo(expectedAccountRecord);
    }
}