package com.afreemov.budgeting.infrastructure.persistence.repository;

import com.afreemov.budgeting.domain.Category;
import com.afreemov.budgeting.domain.Transaction;
import com.afreemov.budgeting.domain.TransactionRepository;
import com.afreemov.budgeting.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionReposityJpa implements TransactionRepository {

    private final TransactionEntityRepository transactionEntityRepository;

    public TransactionReposityJpa(TransactionEntityRepository transactionEntityRepository) {
        this.transactionEntityRepository = transactionEntityRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        var entity = TransactionEntity.from(transaction);

        return transactionEntityRepository.save(entity).toDomain();
    }

    @Override
    public List<Transaction> findAllByCategory(Category category) {
        return transactionEntityRepository.findAllByCategory(category)
                .stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }
}
