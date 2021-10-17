package nl.rabobank.mongo.account;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AccountInternalRepository extends MongoRepository<AccountRecord, String> {

    @Query("{'$or':[ {'accountHolderName':?0}, {'grants.granteeName':?0} ]}")
    List<AccountRecord> findAllAvailableAccounts(String name);
}
