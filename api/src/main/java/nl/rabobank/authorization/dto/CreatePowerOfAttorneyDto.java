package nl.rabobank.authorization.dto;

import lombok.Data;
import nl.rabobank.authorizations.Authorization;

@Data
public class CreatePowerOfAttorneyDto {
    private String granteeName;
    private Authorization authorization;
}
