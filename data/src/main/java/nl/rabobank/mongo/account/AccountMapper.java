package nl.rabobank.mongo.account;

import nl.rabobank.account.Account;
import nl.rabobank.account.AccountType;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    public Account mapToAccount (AccountRecord accountRecord) {
        Account account = null;
        if (AccountType.PAYMENT.name().equals(accountRecord.getType()))  {
            account = new PaymentAccount(accountRecord.getAccountNumber(), accountRecord.getAccountHolderName(), accountRecord.getBalance());
        }
        if (AccountType.SAVINGS.name().equals(accountRecord.getType()))  {
            account =new SavingsAccount(accountRecord.getAccountNumber(), accountRecord.getAccountHolderName(), accountRecord.getBalance());
        }
        return account;
    }

    public AccountRecord mapToAccountRecord (Account account) {
        AccountType type = null;
        if (account instanceof PaymentAccount)  {
            type = AccountType.PAYMENT;
        }
        if (account instanceof SavingsAccount)  {
            type = AccountType.SAVINGS;
        }

        return AccountRecord.builder()
                .accountNumber(account.getAccountNumber())
                .accountHolderName(account.getAccountHolderName())
                .balance(account.getBalance())
                .type(type.name())
                .build();
    }
}
