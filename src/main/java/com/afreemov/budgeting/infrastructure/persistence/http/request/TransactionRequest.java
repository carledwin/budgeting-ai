package com.afreemov.budgeting.infrastructure.persistence.http.request;

import com.afreemov.budgeting.application.input.PersistTransactionInput;
import com.afreemov.budgeting.domain.Category;

import java.math.BigDecimal;

public record TransactionRequest(String description, Category category, BigDecimal amount) {
    public PersistTransactionInput toInput(String sourceOrigin) {
        return new PersistTransactionInput(
                description,
                amount,
                category,
                sourceOrigin,
                "direct-rest-api",
                "anonymous");
    }
}
