package nl.rabobank.account;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountFactory {
    public Account create(String accountNumber, String accountHolderName, AccountType type) {
        switch (type){
            case PAYMENT:
                return  new PaymentAccount(accountNumber, accountHolderName, 0d);
            case SAVINGS:
                return new SavingsAccount(accountNumber, accountHolderName, 0d);
            default:
                log.warn("Invalid account type: {}", type);
                throw new IllegalArgumentException(String.format("Invalid account type: %s", type));
        }
    }
}
