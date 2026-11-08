package com.afreemov.budgeting.application.input;

import com.afreemov.budgeting.domain.Category;
import org.springframework.ai.tool.annotation.ToolParam;

import java.math.BigDecimal;

public record PersistTransactionInput(@ToolParam(description = "Descrição do gasto") String description,
                                      @ToolParam(description = "Valor do gasto (ex: 35.00)") BigDecimal amount,
                                      @ToolParam(description = "Categoria do gasto") Category category,
                                      @ToolParam(description = "Origem do envio (ex: MOBILE, EVENT, API_INTEGRATION)", required = false) String sourceOrigin,
                                      @ToolParam(description = "Nome do arquivo de origem", required = false) String sourceFileName,
                                      @ToolParam(description = "Usuário criador", required = false) String createdBy) {
}
