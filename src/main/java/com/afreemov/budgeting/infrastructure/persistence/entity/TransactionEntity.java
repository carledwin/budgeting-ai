package com.afreemov.budgeting.infrastructure.persistence.entity;

import com.afreemov.budgeting.domain.Category;
import com.afreemov.budgeting.domain.Transaction;
import com.afreemov.budgeting.domain.TransactionId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntity {

    @Id
    private UUID id;
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Category category;

    private LocalDateTime createdAt;
    private String createdBy;
    private String sourceFileName;
    private String sourceOrigin;

    //mapper pattern
    public static TransactionEntity from (Transaction transaction){
        return new TransactionEntity(
                transaction.getId().uuid(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getCategory(),
                transaction.getCreatedAt(),
                transaction.getCreatedBy(),
                transaction.getSourceFileName(),
                transaction.getSourceOrigin()
        );
    }


    public Transaction toDomain() {
        return new Transaction(
                new TransactionId(this.getId()),
                this.getDescription(),
                this.getAmount(),
                this.getCategory(),
                this.getCreatedAt(),
                this.getCreatedBy(),
                this.getSourceFileName(),
                this.getSourceOrigin()
        );
    }
}
