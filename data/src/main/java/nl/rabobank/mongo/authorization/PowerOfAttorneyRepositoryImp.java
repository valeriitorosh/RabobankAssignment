package nl.rabobank.mongo.authorization;

import lombok.RequiredArgsConstructor;
import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.authorizations.interfaces.PowerOfAttorneyRepository;
import nl.rabobank.mongo.account.AccountInternalRepository;
import nl.rabobank.mongo.account.AccountMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
public class PowerOfAttorneyRepositoryImp implements PowerOfAttorneyRepository {

    private final PowerOfAttorneyInternalRepository powerOfAttorneyInternalRepository;
    private final AccountInternalRepository accountInternalRepository;
    private final AccountMapper accountMapper;

    public PowerOfAttorney save(PowerOfAttorney powerOfAttorney) {

        final var powerOfAttorneyRecord = PowerOfAttorneyRecord.builder()
                .accountNumber(powerOfAttorney.getAccount().getAccountNumber())
                .grantorName(powerOfAttorney.getGrantorName())
                .granteeName(powerOfAttorney.getGranteeName())
                .authorization(powerOfAttorney.getAuthorization().name())
                .build();

        powerOfAttorneyInternalRepository.save(powerOfAttorneyRecord);
        return powerOfAttorney;
    }

    public void delete(String grantorName, String granteeName, String accountNumber, Authorization authorization) {
        powerOfAttorneyInternalRepository.deleteByGrantorNameAndGranteeNameAndAccountNumberAndAuthorization(
                grantorName, granteeName, accountNumber, authorization.name());
    }

    public List<Account> findAllAccountsByGranteeName(String granteeName) {
        final var records = powerOfAttorneyInternalRepository.findAllByGranteeName(granteeName);
        final var accountIds = records.stream()
                .map(PowerOfAttorneyRecord::getAccountNumber)
                .collect(Collectors.toSet());
        final var accountRecords = accountInternalRepository.findAllById(accountIds);
        return StreamSupport.stream(accountRecords.spliterator(), false)
                .map(accountMapper::mapToAccount)
                .collect(Collectors.toList());
    }
}
