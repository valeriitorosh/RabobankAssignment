package nl.rabobank.account;

import nl.rabobank.account.dto.AccountDto;
import nl.rabobank.account.exceptions.AccountAlreadyExistsException;
import nl.rabobank.account.exceptions.AccountNotFoundException;
import nl.rabobank.account.exceptions.InvalidAccountGrantorException;
import nl.rabobank.account.exceptions.PowerOfAttorneyAlreadyExistsException;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.dto.PowerOfAttorneyDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreatePowerOfAttorney() throws Exception {
        //Given
        final var accountNumber = "1234-567-89";
        final var accountHolderName = "John Lennon";
        final var type = AccountType.PAYMENT;

        final var accountDto = AccountDto.builder()
                .accountNumber(accountNumber)
                .accountHolderName(accountHolderName)
                .balance(0d)
                .build();


        when(accountService.createAccount(accountNumber, accountHolderName, type))
                .thenReturn(accountDto);

        //When, Then
        mockMvc.perform(post("/clients/{accountHolderName}/accounts", accountHolderName)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"accountNumber\":\"1234-567-89\",\n" +
                        "  \"type\": \"PAYMENT\"\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "  \"accountNumber\": \"1234-567-89\",\n" +
                        "  \"accountHolderName\": \"John Lennon\",\n" +
                        "  \"balance\": 0.0\n" +
                        "}"));
    }

    @Test
    void shouldReturnAccountAlreadyExistsExceptionWhenCreateAndAccountExists() throws Exception {
        //Given
        final var accountNumber = "1234-567-89";
        final var accountHolderName = "John Lennon";
        final var type = AccountType.PAYMENT;

        when(accountService.createAccount(accountNumber, accountHolderName, type))
                .thenThrow(new AccountAlreadyExistsException(String.format("Account with accountNumber: %s already exists.", accountNumber)));

        //When, Then
        mockMvc.perform(post("/clients/{accountHolderName}/accounts", accountHolderName)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"accountNumber\":\"1234-567-89\",\n" +
                        "  \"type\": \"PAYMENT\"\n" +
                        "}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Account with accountNumber: 1234-567-89 already exists."));
    }

    @Test
    void shouldGetAvailableAccounts() throws Exception {
        //Given
        final var name = "John Lennon";

        final var accountDto1 = AccountDto.builder()
                .accountNumber("1234-567-89")
                .accountHolderName("John Lennon")
                .balance(9.99)
                .build();
        final var accountDto2 = AccountDto.builder()
                .accountNumber("1234-567-81")
                .accountHolderName("Ringo Star")
                .balance(9.99)
                .build();


        when(accountService.getAvailableAccounts(name))
                .thenReturn(List.of(accountDto1, accountDto2));

        //When, Then
        mockMvc.perform(get("/clients/{name}/accounts", "John Lennon")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[\n" +
                        "  {\n" +
                        "    \"accountNumber\": \"1234-567-89\",\n" +
                        "    \"accountHolderName\": \"John Lennon\",\n" +
                        "    \"balance\": 9.99\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"accountNumber\": \"1234-567-81\",\n" +
                        "    \"accountHolderName\": \"Ringo Star\",\n" +
                        "    \"balance\": 9.99\n" +
                        "  }\n" +
                        "]"));
    }

    @Test
    void shouldGrantPowerOfAttorney() throws Exception {
        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";

        final var powerOfAttorneyDto = PowerOfAttorneyDto.builder()
                .grantorName(grantorName)
                .granteeName(granteeName)
                .accountNumber(accountNumber)
                .authorization("WRITE")
                .build();

        when(accountService.grantPowerOfAttorney(grantorName, granteeName, accountNumber, Authorization.WRITE))
                .thenReturn(powerOfAttorneyDto);

        //When, Then
        mockMvc.perform(post("/clients/{grantorName}/accounts/{accountNumber}/powerOfAttorney", "John Lennon", "1234-567-89")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"granteeName\":\"Ringo Star\",\n" +
                        "  \"authorization\": \"WRITE\"\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "  \"granteeName\": \"Ringo Star\",\n" +
                        "  \"grantorName\": \"John Lennon\",\n" +
                        "  \"accountNumber\": \"1234-567-89\",\n" +
                        "  \"authorization\": \"WRITE\"\n" +
                        "}"));
    }

    @Test
    void shouldReturnNotFoundWhenGrantAndAccountNotFound() throws Exception {
        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";

        when(accountService.grantPowerOfAttorney(grantorName, granteeName, accountNumber, Authorization.WRITE))
                .thenThrow(new AccountNotFoundException(String.format("Account: %s not found.", accountNumber)));

        //When, Then
        mockMvc.perform(post("/clients/{grantorName}/accounts/{accountNumber}/powerOfAttorney", "John Lennon", "1234-567-89")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"granteeName\":\"Ringo Star\",\n" +
                        "  \"authorization\": \"WRITE\"\n" +
                        "}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account: 1234-567-89 not found."));
    }

    @Test
    void shouldReturnForbiddenWhenGrantAndGrantorIsNotHolder() throws Exception {
        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";

        when(accountService.grantPowerOfAttorney(grantorName, granteeName, accountNumber, Authorization.WRITE))
                .thenThrow(new InvalidAccountGrantorException(String.format("Grantor %s is not the owner of the account: %s.", grantorName, accountNumber)));

        //When, Then
        mockMvc.perform(post("/clients/{grantorName}/accounts/{accountNumber}/powerOfAttorney", grantorName, accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"granteeName\":\"Ringo Star\",\n" +
                        "  \"authorization\": \"WRITE\"\n" +
                        "}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Grantor John Lennon is not the owner of the account: 1234-567-89."));
    }

    @Test
    void shouldReturnConflictWhenGrantAlreadyExists() throws Exception {
        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var authorization = Authorization.WRITE;

        when(accountService.grantPowerOfAttorney(grantorName, granteeName, accountNumber, authorization))
                .thenThrow(new PowerOfAttorneyAlreadyExistsException(
                        String.format("PowerOfAttorney already exists: grantorName = %s, granteeName = %s, accountNumber = %s, authorization = %s.",
                                grantorName, granteeName, accountNumber, authorization)));

        //When, Then
        mockMvc.perform(post("/clients/{grantorName}/accounts/{accountNumber}/powerOfAttorney", grantorName, accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"granteeName\":\"Ringo Star\",\n" +
                        "  \"authorization\": \"WRITE\"\n" +
                        "}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("PowerOfAttorney already exists: grantorName = John Lennon, granteeName = Ringo Star, accountNumber = 1234-567-89, authorization = WRITE."));
    }

    @Test
    void shouldRevokePowerOfAttorney() throws Exception {
        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var authorization = Authorization.WRITE;

        doNothing()
                .when(accountService).revokePowerOfAttorney(grantorName, granteeName, accountNumber, authorization);


        //When, Then
        mockMvc.perform(delete("/clients/{grantorName}/accounts/{accountNumber}/powerOfAttorney", "John Lennon", "1234-567-89")
                .contentType(MediaType.APPLICATION_JSON)
                .param("granteeName", granteeName)
                .param("authorization", authorization.name()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenRevokeAndAccountNotFound() throws Exception {
        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var authorization = Authorization.WRITE;

        doThrow(new AccountNotFoundException(String.format("Account: %s not found.", accountNumber)))
                .when(accountService).revokePowerOfAttorney(grantorName, granteeName, accountNumber, authorization);

        //When, Then
        mockMvc.perform(delete("/clients/{grantorName}/accounts/{accountNumber}/powerOfAttorney", "John Lennon", "1234-567-89")
                .contentType(MediaType.APPLICATION_JSON)
                .param("granteeName", granteeName)
                .param("authorization", authorization.name()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account: 1234-567-89 not found."));
    }

    @Test
    void shouldReturnForbiddenWhenRevokeAndGrantorIsNotHolder() throws Exception {

        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var authorization = Authorization.WRITE;

        doThrow(new InvalidAccountGrantorException(String.format("Grantor %s is not the owner of the account: %s.", grantorName, accountNumber)))
                .when(accountService).revokePowerOfAttorney(grantorName, granteeName, accountNumber, authorization);

        //When, Then
        mockMvc.perform(delete("/clients/{grantorName}/accounts/{accountNumber}/powerOfAttorney", "John Lennon", "1234-567-89")
                .contentType(MediaType.APPLICATION_JSON)
                .param("granteeName", granteeName)
                .param("authorization", authorization.name()))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Grantor John Lennon is not the owner of the account: 1234-567-89."));
    }
}