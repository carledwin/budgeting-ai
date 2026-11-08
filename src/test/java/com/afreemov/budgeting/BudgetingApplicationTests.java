package com.afreemov.budgeting;

import com.afreemov.budgeting.domain.TransactionRepository;
import com.afreemov.budgeting.infrastructure.persistence.repository.TransactionEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
        "spring.ai.openai.api-key=test"
})
class BudgetingApplicationTests {

    @MockitoBean
    private TransactionRepository transactionRepository;

    @MockitoBean
    private TransactionEntityRepository transactionEntityRepository;

	@Test
	void contextLoads() {
	}

}
