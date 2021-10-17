package nl.rabobank.account;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountFactory {

    public Account create(String accountNumber, String accountHolderName, AccountType type, Double balance) {
        switch (type){
            case PAYMENT:
                return  new PaymentAccount(accountNumber, accountHolderName, balance);
            case SAVINGS:
                return new SavingsAccount(accountNumber, accountHolderName, balance);
            default:
                log.warn("Invalid account type: {}", type);
                throw new IllegalArgumentException(String.format("Invalid account type: %s", type));
        }
    }

    public Account create(String accountNumber, String accountHolderName, AccountType type) {
        return create(accountNumber,accountHolderName, type, 0d);
    }
}
