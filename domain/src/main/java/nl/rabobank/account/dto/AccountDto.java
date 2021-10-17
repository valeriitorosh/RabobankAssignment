package nl.rabobank.account.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class AccountDto {
    String accountNumber;
    String accountHolderName;
    Double balance;
}
