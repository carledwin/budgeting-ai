package com.afreemov.budgeting.application.output;

import com.afreemov.budgeting.domain.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record TransactionOutput(String id, String description, String category, BigDecimal amount) {
    public static TransactionOutput from(Transaction transaction) {
        return new TransactionOutput(
                transaction.getId().toString(),
                transaction.getDescription(),
                transaction.getCategory().name(),
                transaction.getAmount() != null ? transaction.getAmount().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2));
    }
}
