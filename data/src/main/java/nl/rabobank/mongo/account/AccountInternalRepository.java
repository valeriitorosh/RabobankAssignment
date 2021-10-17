package nl.rabobank.mongo.account;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AccountInternalRepository extends MongoRepository<AccountRecord, String> {
    List<AccountRecord> findAllByAccountHolderName(String name);
}
