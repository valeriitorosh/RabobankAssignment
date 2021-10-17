package nl.rabobank.mongo.account;

import nl.rabobank.mongo.authorization.PowerOfAttorneyRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class AccountInternalRepositoryTest {
    @Autowired
    private AccountInternalRepository accountInternalRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void shouldSaveAccount() {

        //Given
        final var accountNumber = "1234-567-89";
        final var powerOfAttorneyRecord = PowerOfAttorneyRecord.builder()
                .granteeName("Ringo Star")
                .authorization("WRITE")
                .build();
        final var accountRecord = AccountRecord.builder()
                .accountNumber(accountNumber)
                .accountHolderName("John Lennon")
                .type("SAVINGS")
                .balance(9.99)
                .grants(Set.of(powerOfAttorneyRecord))
                .build();

        //When
        accountInternalRepository.save(accountRecord);

        //Then
        final var result = mongoTemplate.findById(accountNumber, AccountRecord.class);
        assertThat(result).isEqualTo(accountRecord);
    }

    @Test
    void shouldFindAllAvailableAccountsByClientName() {

        //Given
        final var clientName = "John Lennon";
        final var accountRecord1 = AccountRecord.builder()
                .accountNumber("1234-567-89")
                .accountHolderName(clientName)
                .type("SAVINGS")
                .balance(9.99)
                .build();
        mongoTemplate.save(accountRecord1);

        final var accountRecord2 = AccountRecord.builder()
                .accountNumber("1234-567-81")
                .accountHolderName(clientName)
                .type("SAVINGS")
                .balance(9.99)
                .build();
        mongoTemplate.save(accountRecord2);

        final var powerOfAttorneyRecord = PowerOfAttorneyRecord.builder()
                .granteeName(clientName)
                .authorization("WRITE")
                .build();
        final var accountRecord3 = AccountRecord.builder()
                .accountNumber("1234-567-82")
                .accountHolderName("Ringo Star")
                .type("SAVINGS")
                .grants(Set.of(powerOfAttorneyRecord))
                .balance(9.99)
                .build();
        mongoTemplate.save(accountRecord3);

        final var skippedAccountRecord = AccountRecord.builder()
                .accountNumber("1234-567-84")
                .accountHolderName("Ringo Star")
                .type("SAVINGS")
                .balance(9.99)
                .build();
        mongoTemplate.save(skippedAccountRecord);

        //When
        final var result = accountInternalRepository.findAllAvailableAccounts(clientName);

        //Then
        assertThat(result).containsExactlyInAnyOrderElementsOf(List.of(accountRecord1, accountRecord2, accountRecord3));
    }
}