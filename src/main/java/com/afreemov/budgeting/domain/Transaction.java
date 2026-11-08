package com.afreemov.budgeting.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Transaction {

    private TransactionId id;
    private String description;
    private BigDecimal amount;
    private Category category;
    private LocalDateTime createdAt;
    private String createdBy;
    private String sourceFileName;
    private String sourceOrigin;

    public Transaction(String description, BigDecimal amount, Category category){
        this.id = new TransactionId(UUID.randomUUID());
        this.description = description;
        this.amount = amount != null ? amount.setScale(2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2);
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.createdBy = "anonymous";
        this.sourceFileName = "direct-request";
        this.sourceOrigin = "unknown-integration";
    }

    public Transaction(String description, BigDecimal amount, Category category, String sourceOrigin, String sourceFileName, String createdBy){
        this.id = new TransactionId(UUID.randomUUID());
        this.description = description;
        this.amount = amount != null ? amount.setScale(2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2);
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy != null ? createdBy : "anonymous";
        this.sourceFileName = sourceFileName != null ? sourceFileName : "direct-request";
        this.sourceOrigin = sourceOrigin != null && !sourceOrigin.isBlank() ? sourceOrigin : "unknown-integration";
    }
}
