package nl.rabobank.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.dto.AccountDto;
import nl.rabobank.account.exceptions.AccountAlreadyExistsException;
import nl.rabobank.account.interfaces.AccountRepository;
import nl.rabobank.authorizations.interfaces.PowerOfAttorneyRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final PowerOfAttorneyRepository powerOfAttorneyRepository;
    private final AccountRepository accountRepository;
    private final AccountFactory accountFactory;

    public AccountDto createAccount(String accountNumber, String accountHolderName, AccountType type) {
        log.info("Account creation: accountNumber = {}, accountHolderName = {}, AccountType = {}",
                accountNumber, accountHolderName, type);
        final var accountOptional = accountRepository.findByAccountNumber(accountNumber);
        if (accountOptional.isPresent()) {
            final var message = String.format("Account with accountNumber: %s already exists.", accountNumber);
            throw new AccountAlreadyExistsException(message);
        }

        final var account = accountFactory.create(accountNumber, accountHolderName, type);
        final var savedAccount = accountRepository.save(account);

        log.info("Account created: accountNumber = {}, accountHolderName = {}, AccountType = {}",
                accountNumber, accountHolderName, type);
        return AccountDto.builder()
                .accountNumber(savedAccount.getAccountNumber())
                .accountHolderName(savedAccount.getAccountHolderName())
                .balance(savedAccount.getBalance())
                .build();
    }

    public List<AccountDto> getAvailableAccounts(String name) {
        log.info("Getting available accounts for name {}", name);
        final var ownAccounts = accountRepository.findAllByAccountHolderName(name);
        final var grantedAccounts = powerOfAttorneyRepository.findAllAccountsByGranteeName(name);
        final var accountStream = Stream.concat(ownAccounts.stream(), grantedAccounts.stream());

        return accountStream
                .map(r -> AccountDto.builder()
                        .accountNumber(r.getAccountNumber())
                        .accountHolderName(r.getAccountHolderName())
                        .balance(r.getBalance())
                .build())
                .collect(Collectors.toList());
    }
}
