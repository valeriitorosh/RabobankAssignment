package nl.rabobank.mongo.account;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Builder(toBuilder = true)
@Document("accounts")
public class AccountRecord
{
    @Id
    String accountNumber;
    String accountHolderName;
    String type;
    Double balance;
}
