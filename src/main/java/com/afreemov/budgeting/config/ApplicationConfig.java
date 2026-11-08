package com.afreemov.budgeting.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder){
        return builder.build();
    }
}
