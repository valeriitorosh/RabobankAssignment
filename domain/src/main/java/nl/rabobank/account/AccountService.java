package nl.rabobank.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.dto.AccountDto;
import nl.rabobank.account.exceptions.AccountAlreadyExistsException;
import nl.rabobank.account.exceptions.AccountNotFoundException;
import nl.rabobank.account.exceptions.InvalidAccountGrantorException;
import nl.rabobank.account.exceptions.PowerOfAttorneyAlreadyExistsException;
import nl.rabobank.account.interfaces.AccountRepository;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.authorizations.dto.PowerOfAttorneyDto;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class AccountService {
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
        final var accounts = accountRepository.findAllAvailableAccountsByClientsName(name);

        return accounts.stream()
                .map(a -> AccountDto.builder()
                        .accountNumber(a.getAccountNumber())
                        .accountHolderName(a.getAccountHolderName())
                        .balance(a.getBalance())
                .build())
                .collect(Collectors.toList());
    }

    public PowerOfAttorneyDto grantPowerOfAttorney(String grantorName, String granteeName, String accountNumber, Authorization authorization) {
        log.info("Creation PowerOfAttorney: grantorName = {}, granteeName = {}, accountNumber = {}, authorization = {}",
                grantorName, granteeName, accountNumber, authorization);
        final Account account = findAndValidateAccount(grantorName, accountNumber);

        final var powerOfAttorney = PowerOfAttorney.builder()
                .grantorName(grantorName)
                .granteeName(granteeName)
                .account(account)
                .authorization(authorization)
                .build();

        if (account.getGrants().contains(powerOfAttorney)) {
            log.info("PowerOfAttorney already exists: grantorName = {}, granteeName = {}, accountNumber = {}, authorization = {}",
                    grantorName, granteeName, accountNumber, authorization);
            final var message = String.format("PowerOfAttorney already exists: grantorName = %s, granteeName = %s, accountNumber = %s, authorization = %s.",
                    grantorName, granteeName, accountNumber, authorization);
            throw new PowerOfAttorneyAlreadyExistsException(message);
        }

        account.getGrants().add(powerOfAttorney);
        accountRepository.save(account);

        log.info("Created PowerOfAttorney: grantorName = {}, granteeName = {}, accountNumber = {}, authorization = {}",
                grantorName, granteeName, accountNumber, authorization);

        return PowerOfAttorneyDto.builder()
                .accountNumber(powerOfAttorney.getAccount().getAccountNumber())
                .grantorName(powerOfAttorney.getGrantorName())
                .granteeName(powerOfAttorney.getGranteeName())
                .authorization(powerOfAttorney.getAuthorization().name())
                .build();
    }

    public void revokePowerOfAttorney(String grantorName, String granteeName, String accountNumber, Authorization authorization) {
        log.info("Deletion PowerOfAttorney: grantorName = {}, granteeName = {}, accountNumber = {}, authorization = {}",
                grantorName, granteeName, accountNumber, authorization);
        final Account account = findAndValidateAccount(grantorName, accountNumber);

        final var powerOfAttorney = PowerOfAttorney.builder()
                .grantorName(grantorName)
                .granteeName(granteeName)
                .account(account)
                .authorization(authorization)
                .build();

        final var isRemoved = account.getGrants().remove(powerOfAttorney);

        if(isRemoved) {
            accountRepository.save(account);
            log.info("Deleted PowerOfAttorney: grantorName = {}, granteeName = {}, accountNumber = {}, authorization = {}",
                    grantorName, granteeName, accountNumber, authorization);
        } else {
            log.info("PowerOfAttorney has not been deleted (dos not exists): grantorName = {}, granteeName = {}, accountNumber = {}, authorization = {}",
                    grantorName, granteeName, accountNumber, authorization);
        }
    }

    private Account findAndValidateAccount(String grantorName, String accountNumber) {
        final var optionalAccount = accountRepository.findByAccountNumber(accountNumber);
        final var account = optionalAccount
                .orElseThrow(() -> {
                    final var message = String.format("Account: %s not found.", accountNumber);
                    return new AccountNotFoundException(message);});

        if (!account.getAccountHolderName().equals(grantorName)) {
            final var message = String.format("Grantor %s is not the owner of the account: %s.",grantorName, accountNumber);
            throw new InvalidAccountGrantorException(message);
        }
        return account;
    }
}
