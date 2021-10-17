package nl.rabobank.authorization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.authorization.dto.CreatePowerOfAttorneyDto;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorneyService;
import nl.rabobank.authorizations.dto.PowerOfAttorneyDto;
import nl.rabobank.authorizations.exceptions.AccountNotFoundException;
import nl.rabobank.authorizations.exceptions.InvalidAccountGrantorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PowerOfAttorneyController {

    private final PowerOfAttorneyService powerOfAttorneyService;

    @PostMapping(value = "/clients/{grantorName}/accounts/{accountNumber}/powerOfAttorney")
    public ResponseEntity<PowerOfAttorneyDto> create(@PathVariable String grantorName, @PathVariable String accountNumber, @RequestBody CreatePowerOfAttorneyDto dto) {

        log.info("Got create PowerOfAttorney request: grantorName = {}, granteeName = {}, accountNumber = {}, authorization = {}",
                grantorName, dto.getGranteeName(), accountNumber, dto.getAuthorization());

        final var powerOfAttorneyDto = powerOfAttorneyService.createPowerOfAttorney(grantorName,
                dto.getGranteeName(), accountNumber, dto.getAuthorization());

        return ResponseEntity.ok()
                .body(powerOfAttorneyDto);
    }

    @DeleteMapping(value = "/clients/{grantorName}/accounts/{accountNumber}/powerOfAttorney")
    public ResponseEntity<Void> delete(@PathVariable String grantorName, @PathVariable String accountNumber, @RequestParam String granteeName, @RequestParam Authorization authorization) {

        log.info("Got delete PowerOfAttorney request: grantorName = {}, granteeName = {}, accountNumber = {}, authorization = {}",
                grantorName, granteeName, accountNumber, authorization);

        powerOfAttorneyService.deletePowerOfAttorney(grantorName,
                granteeName, accountNumber, authorization);

        return ResponseEntity.ok()
                .build();
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
}
