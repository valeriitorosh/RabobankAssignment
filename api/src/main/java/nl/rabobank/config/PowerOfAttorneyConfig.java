package nl.rabobank.config;

import nl.rabobank.account.interfaces.AccountRepository;
import nl.rabobank.authorizations.PowerOfAttorneyService;
import nl.rabobank.authorizations.interfaces.PowerOfAttorneyRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PowerOfAttorneyConfig {

    @Bean
    public PowerOfAttorneyService powerOfAttorneyService(final PowerOfAttorneyRepository powerOfAttorneyRepository,
                                                 final AccountRepository accountRepository) {
        return new PowerOfAttorneyService(powerOfAttorneyRepository, accountRepository);
    }
}
