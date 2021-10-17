package nl.rabobank.config;

import nl.rabobank.account.AccountFactory;
import nl.rabobank.account.AccountService;
import nl.rabobank.account.interfaces.AccountRepository;
import nl.rabobank.authorizations.interfaces.PowerOfAttorneyRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountConfig {

    @Bean
    public AccountFactory accountFactory () {
        return new AccountFactory();
    }

    @Bean
    public AccountService accountService(final PowerOfAttorneyRepository powerOfAttorneyRepository,
                                         final AccountRepository accountRepository,
                                         final AccountFactory accountFactory) {
        return new AccountService(powerOfAttorneyRepository, accountRepository, accountFactory);
    }
}
