package nl.rabobank.mongo.account;

import nl.rabobank.account.PaymentAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountRepositoryImpTest {

    @Mock
    private AccountInternalRepository accountInternalRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountRepositoryImp accountRepositoryImp;

    @Test
    void shouldSaveAccount() {

        //Given
        final var accountNumber = "1234-567-89";
        final var account = new PaymentAccount(accountNumber, "John Lennon", 9.99);

        final var accountRecord = AccountRecord.builder()
                .accountNumber(accountNumber)
                .accountHolderName("John Lennon")
                .type("PAYMENT")
                .balance(9.99)
                .build();

        when(accountMapper.mapToAccountRecord(account)).thenReturn(accountRecord);

        //When
        accountRepositoryImp.save(account);

        //Then
        verify(accountInternalRepository).save(same(accountRecord));
    }

    @Test
    void shouldFindAllAccountsByAccountHolderName() {

        //Given
        final var accountNumber = "1234-567-89";
        final var accountHolderName = "John Lennon";
        final var accountRecord = AccountRecord.builder()
                .accountNumber(accountNumber)
                .accountHolderName(accountHolderName)
                .type("PAYMENT")
                .balance(9.99)
                .build();
        final var account = new PaymentAccount(accountNumber, "John Lennon", 9.99);

        when(accountInternalRepository.findAllByAccountHolderName(accountHolderName)).thenReturn(List.of(accountRecord));
        when(accountMapper.mapToAccount(accountRecord)).thenReturn(account);

        //When
        final var result = accountRepositoryImp.findAllByAccountHolderName(accountHolderName);

        //Then
        assertThat(result).containsExactlyInAnyOrderElementsOf(List.of(account));
    }
}