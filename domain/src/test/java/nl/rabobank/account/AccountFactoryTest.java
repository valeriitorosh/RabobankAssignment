package nl.rabobank.account;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountFactoryTest {

    private final AccountFactory accountFactory = new AccountFactory();

    @Test
    void shouldCreatePaymentAccount() {

        //Given
        final var accountNumber = "123-456-789";
        final var accountHolderName = "Jhon lennon";

        //When
        final var account = accountFactory.create(accountNumber, accountHolderName, AccountType.PAYMENT);

        //Then
        final var expectedPaymentAccount = new PaymentAccount(accountNumber, accountHolderName);
        assertThat(account).isEqualTo(expectedPaymentAccount);
    }

    @Test
    void shouldCreateSavingsAccount() {

        //Given
        final var accountNumber = "123-456-789";
        final var accountHolderName = "Jhon lennon";
        final var balance = 9.9;

        //When
        final var account = accountFactory.create(accountNumber, accountHolderName, AccountType.SAVINGS, balance);

        //Then
        final var expectedSavingsAccount = new SavingsAccount(accountNumber, accountHolderName, balance);
        assertThat(account).isEqualTo(expectedSavingsAccount);
    }
}