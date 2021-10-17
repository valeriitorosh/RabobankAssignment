package nl.rabobank.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.dto.AccountDto;
import nl.rabobank.account.dto.CreateAccountDto;
import nl.rabobank.account.exceptions.AccountAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @GetMapping(value = "/clients/{name}/accounts")
    public ResponseEntity<List<AccountDto>> getAllAvailableAccounts(@PathVariable String name) {
        log.info("Got  all available accounts request for client: {}", name);

        final var accountDtoList = accountService.getAvailableAccounts(name);

        return ResponseEntity.ok()
                .body(accountDtoList);
    }

    @PostMapping(value = "/clients/{accountHolderName}/accounts")
    public ResponseEntity<AccountDto> create(@PathVariable String accountHolderName, @RequestBody CreateAccountDto dto) {
        log.info("Got create Account request: accountHolderName = {}, accountNumber = {}, type = {}",
                accountHolderName, dto.getAccountNumber(), dto.getType());

        final var accountDtoList = accountService.createAccount(dto.getAccountNumber(), accountHolderName, dto.getType());

        return ResponseEntity.ok()
                .body(accountDtoList);
    }

    @ExceptionHandler(value = {AccountAlreadyExistsException.class})
    public ResponseEntity<String> handleAccountAlreadyExistsException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }
}
