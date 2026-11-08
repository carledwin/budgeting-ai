package com.afreemov.budgeting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiChatModelIT {

    private static final Logger log =
            LoggerFactory.getLogger(OpenAiChatModelIT.class);

    @Test
    void should_receiveResponse_when_chatModel_isCalled(){

        log.info("Executando OpenAiChatModel...");

        var openAiChatOptions = OpenAiChatOptions.builder()
                .model("gpt-4o-mini")
                .temperature(0.8)
                .responseFormat(OpenAiChatModel.ResponseFormat.builder()
                        .type(OpenAiChatModel.ResponseFormat.Type.TEXT)
                        .build())
                .build();

        var openAiChatModel = OpenAiChatModel.builder()
                .options(openAiChatOptions)
                .build();

        var response = openAiChatModel.call("Gere um registro de budgeting, com descrição de gasto, valor em reais e local");

        assertThat(response).isNotEmpty();

        System.out.println(response);
    }



}