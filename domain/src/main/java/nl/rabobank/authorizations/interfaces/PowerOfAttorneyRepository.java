package nl.rabobank.authorizations.interfaces;

import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;

import java.util.List;

public interface PowerOfAttorneyRepository {
    PowerOfAttorney save(PowerOfAttorney powerOfAttorney);

    void delete(String grantorName, String granteeName, String accountNumber, Authorization authorization);

    List<Account> findAllAccountsByGranteeName(String granteeName);
}
