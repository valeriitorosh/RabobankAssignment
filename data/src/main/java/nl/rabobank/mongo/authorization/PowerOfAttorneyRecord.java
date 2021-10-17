package nl.rabobank.mongo.authorization;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class PowerOfAttorneyRecord {
    String granteeName;
    String authorization;
}
