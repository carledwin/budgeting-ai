package com.afreemov.budgeting.controller;

import com.openai.models.audio.AudioResponseFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/transcription")
public class TranscriptionController {

    private static final Logger log =
            LoggerFactory.getLogger(TranscriptionController.class);
    private final ChatClient chatClient;

    @Autowired
    public TranscriptionController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }


    @PostMapping(value = "/openapi-transcription", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String getPrompt(@RequestParam("file") MultipartFile multipartFile){

        log.info("Request Param fileName: [{}]", multipartFile.getName());

        var resource = multipartFile.getResource();

        var openAiAudioTranscriptionOptions = OpenAiAudioTranscriptionOptions.builder()
                .model("whisper-1")
                .language("pt")
                .temperature(0f)
                .responseFormat(AudioResponseFormat.TEXT)
                .prompt("Áudio em português brasileiro." +
                        "Áudio contém descrição de gastros financeiros." +
                        "As frases geralmente contém:" +
                        "- um valor em reais (número + 'reais');" +
                        "- uma ação: (comprei, peguei, paguei, custou, foi, etc)" +
                        "- um local ou estabelecimento: (farmácia, restaurante, padaria, cinema, mercado, etc)" +
                        "Atenção, ao transcrever use 'R$' para representar o valor monetario")
                .build();

        var audioTranscriptionPrompt = new AudioTranscriptionPrompt(resource, openAiAudioTranscriptionOptions);

        var openAiAudioTranscriptionModel = OpenAiAudioTranscriptionModel.builder()
                .build();

        var response = openAiAudioTranscriptionModel
                .call(audioTranscriptionPrompt);

        return response.getResult().getOutput();

    }
}
