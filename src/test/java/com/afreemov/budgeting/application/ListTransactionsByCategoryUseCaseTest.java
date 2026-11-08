package com.afreemov.budgeting.application;

import com.afreemov.budgeting.domain.Category;
import com.afreemov.budgeting.domain.Transaction;
import com.afreemov.budgeting.domain.TransactionId;
import com.afreemov.budgeting.domain.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ListTransactionsByCategoryUseCaseTest {

    private TransactionRepository transactionRepository;
    private ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase;

    @BeforeEach
    void setUp() {
        transactionRepository = Mockito.mock(TransactionRepository.class);
        listTransactionsByCategoryUseCase = new ListTransactionsByCategoryUseCase(transactionRepository);
    }

    @Test
    void shouldListTransactionsByCategorySuccessfully() {
        // Arrange
        var category = Category.GROCERIES;
        var transaction = new Transaction(
                new TransactionId(UUID.randomUUID()),
                "Supermercado",
                new BigDecimal("150.00"),
                category,
                LocalDateTime.now(),
                "user123",
                "audio.mp3",
                "MOBILE"
        );

        when(transactionRepository.findAllByCategory(category)).thenReturn(List.of(transaction));

        // Act
        var result = listTransactionsByCategoryUseCase.execute(category);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).description()).isEqualTo("Supermercado");
        assertThat(result.get(0).category()).isEqualTo("GROCERIES");
        assertThat(result.get(0).amount()).isEqualByComparingTo(new BigDecimal("150.00"));
    }
}
