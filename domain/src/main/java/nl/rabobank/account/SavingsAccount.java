package nl.rabobank.account;

import lombok.Getter;
import nl.rabobank.authorizations.PowerOfAttorney;

import java.util.HashSet;
import java.util.Set;

@Getter
public class SavingsAccount implements Account
{
    String accountNumber;
    String accountHolderName;
    Double balance; // BigDecimal?
    Set<PowerOfAttorney> grants;

    public SavingsAccount(String accountNumber, String accountHolderName) {
        this(accountNumber, accountHolderName, 0d);
    }

    public SavingsAccount(String accountNumber, String accountHolderName, Double balance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.grants = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SavingsAccount that = (SavingsAccount) o;

        return accountNumber.equals(that.accountNumber);
    }

    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }
}
