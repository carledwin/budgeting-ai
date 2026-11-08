package com.afreemov.budgeting.application;

import com.afreemov.budgeting.application.input.PersistTransactionInput;
import com.afreemov.budgeting.application.output.TransactionOutput;
import com.afreemov.budgeting.domain.Transaction;
import com.afreemov.budgeting.domain.TransactionRepository;
import com.afreemov.budgeting.infrastructure.persistence.http.TransactionContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class PersistTransactionUseCase {

    private final TransactionRepository repository;

    public PersistTransactionUseCase(TransactionRepository repository){
        this.repository = repository;
    }

    @Tool(name = "createTransactionTool", description = "Persiste uma nova transação financeira")
    public TransactionOutput execute(PersistTransactionInput input){
        var ctx = TransactionContext.get();

        String origin = input.sourceOrigin() != null && !input.sourceOrigin().isBlank() 
                ? input.sourceOrigin() 
                : (ctx != null && ctx.sourceOrigin() != null ? ctx.sourceOrigin() : "unknown-integration");
                
        String fileName = input.sourceFileName() != null && !input.sourceFileName().isBlank() 
                ? input.sourceFileName() 
                : (ctx != null && ctx.sourceFileName() != null ? ctx.sourceFileName() : "direct-request");
                
        String user = input.createdBy() != null && !input.createdBy().isBlank() 
                ? input.createdBy() 
                : (ctx != null && ctx.createdBy() != null ? ctx.createdBy() : "anonymous");

        var transaction = new Transaction(
                input.description(),
                input.amount(),
                input.category(),
                origin,
                fileName,
                user
        );

        var saved = repository.save(transaction);
        return TransactionOutput.from(saved);
    }
}
