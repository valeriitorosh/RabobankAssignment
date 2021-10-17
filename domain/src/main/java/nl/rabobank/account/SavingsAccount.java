package nl.rabobank.account;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import nl.rabobank.authorizations.PowerOfAttorney;

import java.util.HashSet;
import java.util.Set;

@Getter
@EqualsAndHashCode(of = "accountNumber")
public class SavingsAccount implements Account {
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

}
