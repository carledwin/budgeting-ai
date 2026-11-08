
package com.afreemov.budgeting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(
        named = "SPRING_AI_OPENAI_API_KEY",
        matches = ".+"
)
class OpenAiIntegrationTest {

    private static final Logger log =
            LoggerFactory.getLogger(OpenAiIntegrationTest.class);

    private static final String MODEL = "gpt-4.1-mini";

    private final ChatClient chatClient;

    @Autowired
    public OpenAiIntegrationTest(ChatModel chatModel) {

        log.info("Executando OpenAiChatModel via ChatClient ...");

        this.chatClient = ChatClient.builder(chatModel)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(MODEL)
                        .responseFormat(OpenAiChatModel.ResponseFormat.builder()
                                .type(OpenAiChatModel.ResponseFormat.Type.TEXT)
                                .build())
                        .temperature(0.3)
                )
                .build();
    }

    @Test
    void deveChamarOpenAiERetornarUmaRespostaValida() {

        String pergunta = """
                Gere um registro de budgeting, com descrição de gasto,
                valor em reais e local.
                """;

        ChatResponse response = this.chatClient.prompt()
                .user(pergunta)
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
        log.info("Modelo configurado: {}", MODEL);
        log.info("Modelo utilizado: {}", modeloUtilizado);
        log.info("Tokens de entrada: {}", usage != null
                ? usage.getPromptTokens() : "Indisponível");
        log.info("Tokens de saída: {}", usage != null
                ? usage.getCompletionTokens() : "Indisponível");
        log.info("Total de tokens: {}", usage != null
                ? usage.getTotalTokens() : "Indisponível");
        log.info("=====================================");

        log.info("Resposta da OpenAI:\n{}", resposta);

        assertThat(resposta).isNotBlank();
        assertThat(modeloUtilizado).isNotBlank();
        assertThat(usage).isNotNull();
    }
}
