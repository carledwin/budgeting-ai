package com.afreemov.budgeting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiChatClientIT {

    private static final Logger log =
            LoggerFactory.getLogger(OpenAiChatClientIT.class);

    private final OpenAiChatModel openAiChatModel;

    @Autowired
    public OpenAiChatClientIT(OpenAiChatModel openAiChatModel){
        this.openAiChatModel = openAiChatModel;
    }

    @Test
    void should_executeSum_when_prompted(){

        log.info("Executando OpenAiChatClient ...");

        var chatClient = ChatClient.builder(openAiChatModel)
                .defaultSystem("Você é um matemático")
                .build();

        var response = chatClient.prompt("Some 10 mais 20. Depois subtraia 30 do resultado anterior. Exiba apenas o resultado.")
                .call()
                .content();

        assertThat(response)
                .isNotEmpty()
                .contains("0");

        System.out.println("Somando 10 + 20 - 30 = " + response);
    }



}