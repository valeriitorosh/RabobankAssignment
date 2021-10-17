package nl.rabobank.mongo.authorization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class PowerOfAttorneyInternalRepositoryTest {
    @Autowired
    private PowerOfAttorneyInternalRepository powerOfAttorneyInternalRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(PowerOfAttorneyRecord.class);
    }

    @Test
    void shouldFindAllAccountsByAccountHolderName() {

        //Given
        final var name = "John Lennon";
        final var powerOfAttorneyRecord1 = PowerOfAttorneyRecord.builder()
                .granteeName(name)
                .grantorName("Ringo Star")
                .accountNumber("1234-567-89")
                .authorization("WRITE")
                .build();
        mongoTemplate.save(powerOfAttorneyRecord1);

        final var powerOfAttorneyRecord2 = PowerOfAttorneyRecord.builder()
                .granteeName(name)
                .grantorName("Paul McCartney")
                .accountNumber("1234-567-81")
                .authorization("READ")
                .build();
        mongoTemplate.save(powerOfAttorneyRecord2);

        final var skippedPowerOfAttorneyRecord = PowerOfAttorneyRecord.builder()
                .granteeName("Ringo Star")
                .grantorName("Paul McCartney")
                .accountNumber("1234-567-82")
                .authorization("READ")
                .build();
        mongoTemplate.save(skippedPowerOfAttorneyRecord);

        //When
        final var result = powerOfAttorneyInternalRepository.findAllByGranteeName(name);

        //Then
        assertThat(result).containsExactlyInAnyOrderElementsOf(List.of(powerOfAttorneyRecord1, powerOfAttorneyRecord2));
    }


    @Test
    void shouldDeleteAllAccountsByGrantorNameAndGranteeNameAndAccountNumberAndAuthorization() {

        //Given
        final var name = "John Lennon";
        final var powerOfAttorneyRecord1 = PowerOfAttorneyRecord.builder()
                .granteeName(name)
                .grantorName("Ringo Star")
                .accountNumber("1234-567-89")
                .authorization("WRITE")
                .build();
        mongoTemplate.save(powerOfAttorneyRecord1);

        final var powerOfAttorneyRecord2 = PowerOfAttorneyRecord.builder()
                .granteeName(name)
                .grantorName("Paul McCartney")
                .accountNumber("1234-567-81")
                .authorization("READ")
                .build();
        mongoTemplate.save(powerOfAttorneyRecord2);

        final var toDelete = PowerOfAttorneyRecord.builder()
                .granteeName("Ringo Star")
                .grantorName("Paul McCartney")
                .accountNumber("1234-567-82")
                .authorization("READ")
                .build();
        mongoTemplate.save(toDelete);

        //When
        powerOfAttorneyInternalRepository.deleteByGrantorNameAndGranteeNameAndAccountNumberAndAuthorization(
                toDelete.getGrantorName(), toDelete.getGranteeName(), toDelete.getAccountNumber(), toDelete.getAuthorization());
        final var result = mongoTemplate.findAll(PowerOfAttorneyRecord.class);

        //Then
        assertThat(result).containsExactlyInAnyOrderElementsOf(List.of(powerOfAttorneyRecord1, powerOfAttorneyRecord2));
    }
}