package nl.rabobank.account.dto;

import lombok.Data;
import nl.rabobank.account.AccountType;

@Data
public class CreateAccountDto {
    private String accountNumber;
    private AccountType type;
}
