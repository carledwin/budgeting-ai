package com.afreemov.budgeting.application;

import com.afreemov.budgeting.application.input.PersistTransactionInput;
import com.afreemov.budgeting.domain.Category;
import com.afreemov.budgeting.domain.Transaction;
import com.afreemov.budgeting.domain.TransactionId;
import com.afreemov.budgeting.domain.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PersistTransactionUseCaseTest {

    private TransactionRepository transactionRepository;
    private PersistTransactionUseCase persistTransactionUseCase;

    @BeforeEach
    void setUp() {
        transactionRepository = Mockito.mock(TransactionRepository.class);
        persistTransactionUseCase = new PersistTransactionUseCase(transactionRepository);
    }

    @Test
    void shouldPersistTransactionSuccessfully() {
        // Arrange
        var input = new PersistTransactionInput("Almoço", new BigDecimal("35.00"), Category.GROCERIES, "MOBILE", "audio.mp3", "user123");
        var savedTransaction = new Transaction(
                new TransactionId(UUID.randomUUID()),
                "Almoço",
                new BigDecimal("35.00"),
                Category.GROCERIES,
                LocalDateTime.now(),
                "user123",
                "audio.mp3",
                "MOBILE"
        );

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        // Act
        var output = persistTransactionUseCase.execute(input);

        // Assert
        assertThat(output).isNotNull();
        assertThat(output.description()).isEqualTo("Almoço");
        assertThat(output.category()).isEqualTo("GROCERIES");
        assertThat(output.amount()).isEqualByComparingTo(new BigDecimal("35.00"));
    }
}
