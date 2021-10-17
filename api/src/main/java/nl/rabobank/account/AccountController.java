package nl.rabobank.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.dto.AccountDto;
import nl.rabobank.account.dto.CreateAccountDto;
import nl.rabobank.account.exceptions.AccountAlreadyExistsException;
import nl.rabobank.account.exceptions.AccountNotFoundException;
import nl.rabobank.account.exceptions.InvalidAccountGrantorException;
import nl.rabobank.account.exceptions.PowerOfAttorneyAlreadyExistsException;
import nl.rabobank.authorization.dto.CreatePowerOfAttorneyDto;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.dto.PowerOfAttorneyDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping(value = "/clients/{grantorName}/accounts/{accountNumber}/powerOfAttorney")
    public ResponseEntity<PowerOfAttorneyDto> create(@PathVariable String grantorName, @PathVariable String accountNumber, @RequestBody CreatePowerOfAttorneyDto dto) {

        log.info("Got create PowerOfAttorney request: grantorName = {}, granteeName = {}, accountNumber = {}, authorization = {}",
                grantorName, dto.getGranteeName(), accountNumber, dto.getAuthorization());

        final var powerOfAttorneyDto = accountService.grantPowerOfAttorney(grantorName,
                dto.getGranteeName(), accountNumber, dto.getAuthorization());

        return ResponseEntity.ok()
                .body(powerOfAttorneyDto);
    }

    @DeleteMapping(value = "/clients/{grantorName}/accounts/{accountNumber}/powerOfAttorney")
    public ResponseEntity<Void> delete(@PathVariable String grantorName, @PathVariable String accountNumber, @RequestParam String granteeName, @RequestParam Authorization authorization) {

        log.info("Got delete PowerOfAttorney request: grantorName = {}, granteeName = {}, accountNumber = {}, authorization = {}",
                grantorName, granteeName, accountNumber, authorization);

        accountService.revokePowerOfAttorney(grantorName, granteeName, accountNumber, authorization);

        return ResponseEntity.ok()
                .build();
    }

    @ExceptionHandler(value = {AccountAlreadyExistsException.class})
    public ResponseEntity<String> handleAccountAlreadyExistsException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }

    @ExceptionHandler(value = {AccountNotFoundException.class})
    public ResponseEntity<String> handleAccountNotFoundException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(value = {InvalidAccountGrantorException.class})
    public ResponseEntity<String> handleInvalidAccountGrantorException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }

    @ExceptionHandler(value = {PowerOfAttorneyAlreadyExistsException.class})
    public ResponseEntity<String> handlePowerOfAttorneyAlreadyExistsException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }
}
