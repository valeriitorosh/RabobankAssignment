package nl.rabobank.authorization;


import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorneyService;
import nl.rabobank.authorizations.dto.PowerOfAttorneyDto;
import nl.rabobank.authorizations.exceptions.AccountNotFoundException;
import nl.rabobank.authorizations.exceptions.InvalidAccountGrantorException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PowerOfAttorneyController.class)
class PowerOfAttorneyControllerTest {

    @MockBean
    private PowerOfAttorneyService powerOfAttorneyService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreatePowerOfAttorney() throws Exception {
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

        when(powerOfAttorneyService.createPowerOfAttorney(grantorName, granteeName, accountNumber, Authorization.WRITE))
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
    void shouldReturnNotFoundWhenCreateAndAccountNotFound() throws Exception {
        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";

        when(powerOfAttorneyService.createPowerOfAttorney(grantorName, granteeName, accountNumber, Authorization.WRITE))
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
    void shouldReturnForbiddenWhenCreateAndGrantorIsNotHolder() throws Exception {
        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";

        when(powerOfAttorneyService.createPowerOfAttorney(grantorName, granteeName, accountNumber, Authorization.WRITE))
                .thenThrow(new InvalidAccountGrantorException(String.format("Grantor %s is not the owner of the account: %s.",grantorName, accountNumber)));

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
    void shouldDeletePowerOfAttorney() throws Exception {
        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var authorization = Authorization.WRITE;

        doNothing()
                .when(powerOfAttorneyService).deletePowerOfAttorney(grantorName, granteeName, accountNumber, authorization);


        //When, Then
        mockMvc.perform(delete("/clients/{grantorName}/accounts/{accountNumber}/powerOfAttorney", "John Lennon", "1234-567-89")
                .contentType(MediaType.APPLICATION_JSON)
                .param("granteeName", granteeName)
                .param("authorization", authorization.name()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenDeleteAndAccountNotFound() throws Exception {
        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var authorization = Authorization.WRITE;

        doThrow(new AccountNotFoundException(String.format("Account: %s not found.", accountNumber)))
                .when(powerOfAttorneyService).deletePowerOfAttorney(grantorName, granteeName, accountNumber, authorization);

        //When, Then
        mockMvc.perform(delete("/clients/{grantorName}/accounts/{accountNumber}/powerOfAttorney", "John Lennon", "1234-567-89")
                .contentType(MediaType.APPLICATION_JSON)
                .param("granteeName", granteeName)
                .param("authorization", authorization.name()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account: 1234-567-89 not found."));
    }

    @Test
    void shouldReturnForbiddenWhenDeleteAndGrantorIsNotHolder() throws Exception {

        //Given
        final var accountNumber = "1234-567-89";
        final var grantorName = "John Lennon";
        final var granteeName = "Ringo Star";
        final var authorization = Authorization.WRITE;

        doThrow(new InvalidAccountGrantorException(String.format("Grantor %s is not the owner of the account: %s.",grantorName, accountNumber)))
                .when(powerOfAttorneyService).deletePowerOfAttorney(grantorName, granteeName, accountNumber, authorization);

        //When, Then
        mockMvc.perform(delete("/clients/{grantorName}/accounts/{accountNumber}/powerOfAttorney", "John Lennon", "1234-567-89")
                .contentType(MediaType.APPLICATION_JSON)
                .param("granteeName", granteeName)
                .param("authorization", authorization.name()))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Grantor John Lennon is not the owner of the account: 1234-567-89."));
    }
}