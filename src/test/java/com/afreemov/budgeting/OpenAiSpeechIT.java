package com.afreemov.budgeting;

import com.openai.models.audio.AudioResponseFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.time.LocalTime.now;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiSpeechIT {

    private static final Logger log =
            LoggerFactory.getLogger(OpenAiSpeechIT.class);


    @Test
    void should_produceAudio_when_text_isProvided() throws IOException {

        log.info("Executando OpenAiSpeechIT ...");

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

        String text = """
            O valor no almoço no restaurante ficou no total de 280 reais e 70 centavos.
            Posso confirmar o pagamento?
            """;

        // Gera o áudio
        byte[] audio = speechModel.call(text);

        // Valida o retorno
        assertThat(audio)
                .isNotNull()
                .isNotEmpty()
                .hasSizeGreaterThan(1024);

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

        // Valida o arquivo gerado
        assertThat(outputFile).exists();
        assertThat(outputFile).isRegularFile();
        assertThat(Files.size(outputFile)).isGreaterThan(1024);

        log.info("Áudio gerado: {}", outputFile.toAbsolutePath());
        log.info("Tamanho do arquivo: {} bytes", Files.size(outputFile));
    }


}