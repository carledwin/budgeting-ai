package com.afreemov.budgeting;

import com.openai.models.audio.AudioResponseFormat;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiTranscriptionIT {

    private static final Logger log =
            LoggerFactory.getLogger(OpenAiTranscriptionIT.class);

    @ParameterizedTest
    @CsvSource({"audio_cinema_pipoca.mp3, R$ 35",
            "audio_lavagem_carro.mp3, R$ 119",
            "audio_padaria_paoleite.mp3, R$ 23"

    })
    void should_containExpectedKeywords_when_audioFilesAreProcessed(String fileName, String expectedKeyword){

        log.info("Executando OpenAiTranscriptionIT ...");

        var recording = new ClassPathResource("audios/"+fileName);

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

        var audioTranscriptionPrompt = new AudioTranscriptionPrompt(recording, openAiAudioTranscriptionOptions);

        var openAiAudioTranscriptionModel = OpenAiAudioTranscriptionModel.builder()
                .build();

        var response = openAiAudioTranscriptionModel
                .call(audioTranscriptionPrompt);

        var result = response.getResult().getOutput();

        assertThat(result).isNotEmpty();
        assertThat(result).contains(expectedKeyword);

        System.out.println("Result: " + result);
    }

}