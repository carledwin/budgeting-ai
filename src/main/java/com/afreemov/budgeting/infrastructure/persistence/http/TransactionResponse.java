package com.afreemov.budgeting.infrastructure.persistence.http;

import com.afreemov.budgeting.application.output.TransactionOutput;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record TransactionResponse(String id, String category, String description, BigDecimal amount) {
    public static TransactionResponse from(TransactionOutput transactionOutput) {
        return new TransactionResponse(
                transactionOutput.id(),
                transactionOutput.category(),
                transactionOutput.description(),
                transactionOutput.amount() != null ? transactionOutput.amount().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2));
    }
}
