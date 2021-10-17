package nl.rabobank.authorizations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.Account;
import nl.rabobank.account.interfaces.AccountRepository;
import nl.rabobank.authorizations.dto.PowerOfAttorneyDto;
import nl.rabobank.authorizations.exceptions.AccountNotFoundException;
import nl.rabobank.authorizations.exceptions.InvalidAccountGrantorException;
import nl.rabobank.authorizations.interfaces.PowerOfAttorneyRepository;

@RequiredArgsConstructor
@Slf4j
public class PowerOfAttorneyService {
    private final PowerOfAttorneyRepository powerOfAttorneyRepository;
    private final AccountRepository accountRepository;

    public PowerOfAttorneyDto createPowerOfAttorney(String grantorName, String granteeName, String accountNumber, Authorization authorization) {
        log.info("Creation PowerOfAttorney: grantorName = {}, granteeName = {}, accountNumber = {}, authorization = {}",
                grantorName, granteeName, accountNumber, authorization);
        final Account account = findAndValidateAccount(grantorName, accountNumber);

        final var powerOfAttorney = PowerOfAttorney.builder()
                .grantorName(grantorName)
                .granteeName(granteeName)
                .account(account)
                .authorization(authorization)
                .build();

        powerOfAttorneyRepository.save(powerOfAttorney);

        log.info("Created PowerOfAttorney: grantorName = {}, granteeName = {}, accountNumber = {}, authorization = {}",
                grantorName, granteeName, accountNumber, authorization);

        return PowerOfAttorneyDto.builder()
                .accountNumber(powerOfAttorney.getAccount().getAccountNumber())
                .grantorName(powerOfAttorney.getGrantorName())
                .granteeName(powerOfAttorney.getGranteeName())
                .authorization(powerOfAttorney.getAuthorization().name())
                .build();
    }

    public void deletePowerOfAttorney(String grantorName, String granteeName, String accountNumber, Authorization authorization) {
        log.info("Deletion PowerOfAttorney: grantorName = {}, granteeName = {}, accountNumber = {}, authorization = {}",
                grantorName, granteeName, accountNumber, authorization);
        findAndValidateAccount(grantorName, accountNumber);
        powerOfAttorneyRepository.delete(grantorName, granteeName, accountNumber, authorization);
        log.info("Deleted PowerOfAttorney: grantorName = {}, granteeName = {}, accountNumber = {}, authorization = {}",
                grantorName, granteeName, accountNumber, authorization);
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
