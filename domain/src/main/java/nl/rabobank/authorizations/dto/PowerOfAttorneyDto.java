package nl.rabobank.authorizations.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class PowerOfAttorneyDto {
    String granteeName;
    String grantorName;
    String accountNumber;
    String authorization;
}
