package nl.rabobank.mongo.account;

import lombok.RequiredArgsConstructor;
import nl.rabobank.account.Account;
import nl.rabobank.account.interfaces.AccountRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AccountRepositoryImp implements AccountRepository {

    private final AccountInternalRepository repository;
    private final AccountMapper accountMapper;

    public Account save(Account account) {
        final var accountRecord = accountMapper.mapToAccountRecord(account);

        repository.save(accountRecord);
        return account;
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        final var accountRecordOptional = repository.findById(accountNumber);
        return accountRecordOptional.map(accountMapper::mapToAccount);
    }

    public List<Account> findAllAvailableAccountsByClientsName (String accountHolderName) {

        final var accountRecords = repository.findAllAvailableAccounts(accountHolderName);
        return accountRecords.stream()
                .map(accountMapper::mapToAccount)
                .collect(Collectors.toList());
    }
}
