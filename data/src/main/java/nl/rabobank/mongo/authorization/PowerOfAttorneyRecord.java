package nl.rabobank.mongo.authorization;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Builder(toBuilder = true)
@Document
@CompoundIndexes({
        @CompoundIndex(name = "unique_record",
                def = "{'granteeName' : 1, 'grantorName': 1, 'accountNumber': 1, 'authorization': 1}", unique = true)})
public class PowerOfAttorneyRecord
{
    String granteeName;
    String grantorName;
    String accountNumber;
    String authorization;
}
