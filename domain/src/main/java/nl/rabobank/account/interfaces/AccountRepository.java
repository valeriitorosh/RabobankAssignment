package nl.rabobank.account.interfaces;

import nl.rabobank.account.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Account save(Account account);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findAllAvailableAccountsByClientsName (String accountHolderName);
}
