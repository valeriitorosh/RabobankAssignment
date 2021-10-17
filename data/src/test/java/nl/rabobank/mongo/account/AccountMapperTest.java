package nl.rabobank.mongo.account;

import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountMapperTest {

    private final AccountMapper accountMapper = new AccountMapper();

    @Test
    void shouldMapAccountRecordToSavingsAccount() {

        //Given
        final var accountNumber = "1234-567-89";
        final var accountHolderName = "John Lennon";
        final var balance = 9.99;
        final var accountRecord = AccountRecord.builder()
                .type("SAVINGS")
                .accountNumber(accountNumber)
                .accountHolderName(accountHolderName)
                .balance(balance)
                .build();

        //When
        final var account = accountMapper.mapToAccount(accountRecord);

        //Then
        final var expectedSavingsAccount = new SavingsAccount(accountNumber, accountHolderName, balance);
        assertThat(account).isEqualTo(expectedSavingsAccount);
    }

    @Test
    void shouldMapAccountRecordToPaymentsAccount() {

        //Given
        final var accountNumber = "1234-567-89";
        final var accountHolderName = "John Lennon";
        final var balance = 9.99;
        final var accountRecord = AccountRecord.builder()
                .type("PAYMENT")
                .accountNumber(accountNumber)
                .accountHolderName(accountHolderName)
                .balance(balance)
                .build();

        //When
        final var account = accountMapper.mapToAccount(accountRecord);

        //Then
        final var expectedPaymentsAccount = new PaymentAccount(accountNumber, accountHolderName, balance);
        assertThat(account).isEqualTo(expectedPaymentsAccount);
    }

    @Test
    void shouldMapSavingsAccountToAccountRecord() {

        //Given
        final var accountNumber = "1234-567-89";
        final var accountHolderName = "John Lennon";
        final var balance = 9.99;
        final var savingsAccount = new SavingsAccount(accountNumber, accountHolderName, balance);

        //When
        final var accountRecord = accountMapper.mapToAccountRecord(savingsAccount);

        //Then
        final var expectedAccountRecord = AccountRecord.builder()
                .type("SAVINGS")
                .accountNumber(accountNumber)
                .accountHolderName(accountHolderName)
                .balance(balance)
                .build();
        assertThat(accountRecord).isEqualTo(expectedAccountRecord);
    }

    @Test
    void shouldMapPaymentAccountToAccountRecord() {

        //Given
        final var accountNumber = "1234-567-89";
        final var accountHolderName = "John Lennon";
        final var balance = 9.99;
        final var paymentAccount = new PaymentAccount(accountNumber, accountHolderName, balance);

        //When
        final var accountRecord = accountMapper.mapToAccountRecord(paymentAccount);

        //Then
        final var expectedAccountRecord = AccountRecord.builder()
                .type("PAYMENT")
                .accountNumber(accountNumber)
                .accountHolderName(accountHolderName)
                .balance(balance)
                .build();
        assertThat(accountRecord).isEqualTo(expectedAccountRecord);
    }
}