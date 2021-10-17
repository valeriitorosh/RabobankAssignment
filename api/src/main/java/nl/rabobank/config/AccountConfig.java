package nl.rabobank.config;

import nl.rabobank.account.AccountFactory;
import nl.rabobank.account.AccountService;
import nl.rabobank.account.interfaces.AccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountConfig {

    @Bean
    public AccountService accountService(final AccountRepository accountRepository,
                                         final AccountFactory accountFactory) {
        return new AccountService(accountRepository, accountFactory);
    }
}
