package com.afreemov.budgeting.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat-client")
public class ChatClientController {

    private static final Logger log =
            LoggerFactory.getLogger(ChatClientController.class);
    private final ChatClient chatClient;

    @Autowired
    public ChatClientController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }


    @GetMapping("/openapi")
    public void getPrompt(@RequestParam String prompt){

        log.info("Request Param: [{}]", prompt);

        ChatResponse response = this.chatClient.prompt()
                .user(prompt)
                .call()
                .chatResponse();

        String resposta = response.getResult()
                .getOutput()
                .getText();

        // Metadados da resposta
        var metadata = response.getMetadata();

        // Modelo efetivamente utilizado
        String modeloUtilizado = metadata.getModel();

        // Informações de consumo
        var usage = metadata.getUsage();

        log.info("========== OPENAI RESPONSE ==========");
        log.info("Modelo utilizado: {}", modeloUtilizado);
        log.info("Tokens de entrada: {}", usage != null
                ? usage.getPromptTokens() : "Indisponível");
        log.info("Tokens de saída: {}", usage != null
                ? usage.getCompletionTokens() : "Indisponível");
        log.info("Total de tokens: {}", usage != null
                ? usage.getTotalTokens() : "Indisponível");
        log.info("=====================================");

        log.info("Resposta da OpenAI:\n{}", resposta);
    }
}
