package com.afreemov.budgeting.application;

import com.afreemov.budgeting.application.input.PersistTransactionInput;
import com.afreemov.budgeting.application.output.TransactionOutput;
import com.afreemov.budgeting.domain.Category;
import com.afreemov.budgeting.domain.Transaction;
import com.afreemov.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListTransactionsByCategoryUseCase {
    private final TransactionRepository repository;

    public ListTransactionsByCategoryUseCase(TransactionRepository repository){
        this.repository = repository;
    }

    @Tool(name = "listTransactionsByCategoryTool", description = "Lista transações financeiras existentes por categoria")
    public List<TransactionOutput> execute(@ToolParam(description = "Categoria de uma transação") Category catergory){
        return repository.findAllByCategory(catergory)
                .stream()
                .map(TransactionOutput::from)
                .toList();
    }
}
