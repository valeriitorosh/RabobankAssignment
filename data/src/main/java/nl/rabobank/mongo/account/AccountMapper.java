package nl.rabobank.mongo.account;

import lombok.RequiredArgsConstructor;
import nl.rabobank.account.Account;
import nl.rabobank.account.AccountFactory;
import nl.rabobank.account.AccountType;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.mongo.authorization.PowerOfAttorneyRecord;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AccountMapper {
    private final AccountFactory accountFactory;

    public Account mapToAccount(AccountRecord accountRecord) {

        final var account = accountFactory.create(accountRecord.getAccountNumber(), accountRecord.getAccountHolderName(), AccountType.valueOf(accountRecord.getType()), accountRecord.getBalance());

        final var grants = accountRecord.getGrants().stream()
                .map(p -> PowerOfAttorney.builder()
                        .account(account)
                        .grantorName(account.getAccountHolderName())
                        .granteeName(p.getGranteeName())
                        .authorization(Authorization.valueOf(p.getAuthorization()))
                        .build())
                .collect(Collectors.toSet());
        account.getGrants().addAll(grants);
        return account;
    }

    public AccountRecord mapToAccountRecord(Account account) {
        AccountType type = null;
        if (account instanceof PaymentAccount) {
            type = AccountType.PAYMENT;
        }
        if (account instanceof SavingsAccount) {
            type = AccountType.SAVINGS;
        }

        final var grants = account.getGrants().stream()
                .map(p -> PowerOfAttorneyRecord.builder()
                        .granteeName(p.getGranteeName())
                        .authorization(p.getAuthorization().name())
                        .build())
                .collect(Collectors.toSet());

        return AccountRecord.builder()
                .accountNumber(account.getAccountNumber())
                .accountHolderName(account.getAccountHolderName())
                .balance(account.getBalance())
                .type(type.name())
                .grants(grants)
                .build();
    }
}
