package com.afreemov.budgeting.controller;

import com.openai.models.audio.AudioResponseFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.time.LocalTime.now;

@RestController
@RequestMapping("/api/text-to-speech")
public class TextToSpeechController {

    private static final Logger log =
            LoggerFactory.getLogger(TextToSpeechController.class);
    private final ChatClient chatClient;

    @Autowired
    public TextToSpeechController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }


    @PostMapping(value = "/openapi-sinthesize", produces = "audio/mp3")
    public ResponseEntity<Resource> getPrompt(@RequestBody SinthesizeRequest sinthesizeRequest) throws IOException {

        log.info("Request recebida ...");

        var message =  sinthesizeRequest.message;

        var options = OpenAiAudioSpeechOptions.builder()
                .model("gpt-4o-mini-tts")
                .voice(OpenAiAudioSpeechOptions.Voice.NOVA)
                .speed(1.2)
                .responseFormat(
                        OpenAiAudioSpeechOptions.AudioResponseFormat.MP3
                )
                .instructions("""
                    Fale em português brasileiro.
                    Utilize uma voz feminina, natural e descontraída.
                    Mantenha uma pronúncia clara e pausas naturais.
                    """)
                .build();

        var speechModel = OpenAiAudioSpeechModel.builder()
                .options(options)
                .build();


        // Gera o áudio
        byte[] audio = speechModel.call(message);

        // Define o diretório de saída
        Path audioDirectory = Path.of(
                "src/test/resources/audios-output"
        );

        // Cria o diretório caso não exista
        Files.createDirectories(audioDirectory);

        // Define o arquivo MP3
        Path outputFile = audioDirectory.resolve("AUDIO_output_"+now()+".mp3");

        // Grava o áudio no disco
        Files.write(outputFile, audio);

        log.info("Áudio gerado: {}", outputFile.toAbsolutePath());
        log.info("Tamanho do arquivo: {} bytes", Files.size(outputFile));

        var byteArrayResource = new ByteArrayResource(audio);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("AUDIO_output_"+now()+".mp3")
                                .build().toString())
                .body(byteArrayResource);

    }

    public record SinthesizeRequest(String message){

    }
}
