package nl.rabobank.mongo.account;

import lombok.Builder;
import lombok.Value;
import nl.rabobank.mongo.authorization.PowerOfAttorneyRecord;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Value
@Builder(toBuilder = true)
@Document("accounts")
@CompoundIndexes({
        @CompoundIndex(name = "available_accounts",
                def = "{'accountHolderName' : 1, 'grants.granteeName' : 1, 'grants.authorization': 1}")
})
public class AccountRecord
{
    @Id
    String accountNumber;
    String accountHolderName;
    String type;
    Double balance;
    Set<PowerOfAttorneyRecord> grants;
}
