package nl.rabobank.mongo.authorization;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

interface PowerOfAttorneyInternalRepository extends MongoRepository<PowerOfAttorneyRecord, String> {
    List<PowerOfAttorneyRecord> findAllByGranteeName(String granteeName);

    void deleteByGrantorNameAndGranteeNameAndAccountNumberAndAuthorization(String grantorName, String granteeName, String accountNumber, String authorization);
}
