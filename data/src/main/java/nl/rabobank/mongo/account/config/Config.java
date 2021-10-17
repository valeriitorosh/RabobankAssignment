package nl.rabobank.mongo.account.config;

import nl.rabobank.account.AccountFactory;
import nl.rabobank.mongo.account.AccountInternalRepository;
import nl.rabobank.mongo.account.AccountMapper;
import nl.rabobank.mongo.account.AccountRepositoryImp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public AccountFactory accountFactory() {
        return new AccountFactory();
    }

    @Bean
    public AccountMapper accountMapper(AccountFactory accountFactory) {
        return new AccountMapper(accountFactory);
    }

    @Bean
    public AccountRepositoryImp accountRepository(AccountInternalRepository accountInternalRepository,
                                                     AccountMapper accountMapper) {
        return new AccountRepositoryImp(accountInternalRepository, accountMapper);
    }
}
