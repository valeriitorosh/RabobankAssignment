package nl.rabobank.account;

import lombok.Getter;
import nl.rabobank.authorizations.PowerOfAttorney;

import java.util.HashSet;
import java.util.Set;

@Getter
public class PaymentAccount implements Account
{
    String accountNumber;
    String accountHolderName;
    Double balance; // BigDecimal?
    Set<PowerOfAttorney> grants;

    public PaymentAccount(String accountNumber, String accountHolderName) {
        this(accountNumber, accountHolderName, 0d);
    }

    public PaymentAccount(String accountNumber, String accountHolderName, Double balance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.grants = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentAccount that = (PaymentAccount) o;

        return accountNumber.equals(that.accountNumber);
    }

    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }
}
