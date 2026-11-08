package com.afreemov.budgeting.infrastructure.persistence.http;


import com.afreemov.budgeting.application.ListTransactionsByCategoryUseCase;
import com.afreemov.budgeting.application.PersistTransactionUseCase;
import com.afreemov.budgeting.application.input.PersistTransactionInput;
import com.afreemov.budgeting.application.output.TransactionOutput;
import com.afreemov.budgeting.controller.TranscriptionController;
import com.afreemov.budgeting.domain.Category;
import com.afreemov.budgeting.infrastructure.persistence.http.request.TransactionRequest;
import com.openai.models.audio.AudioResponseFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.time.LocalTime.now;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    private static final Logger log =
            LoggerFactory.getLogger(TransactionController.class);

    private final PersistTransactionUseCase persistTransactionUseCase;
    private final ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase;
    private final ChatClient chatClient;

    public TransactionController(PersistTransactionUseCase persistTransactionUseCase,
                                 ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase,
                                 ChatClient.Builder chatClientBuilder,
                                 @Value("classpath:prompts/system-message.st") Resource systemPrompt) throws IOException {
        this.persistTransactionUseCase = persistTransactionUseCase;
        this.listTransactionsByCategoryUseCase = listTransactionsByCategoryUseCase;
        this.chatClient = chatClientBuilder
                .defaultTools(persistTransactionUseCase, listTransactionsByCategoryUseCase)
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(
            @RequestBody TransactionRequest transactionRequest,
            @RequestHeader(value = "X-Source-Origin", defaultValue = "unknown-integration") String sourceOrigin){
        var transaction = persistTransactionUseCase.execute(transactionRequest.toInput(sourceOrigin));
        return TransactionResponse.from(transaction);
    }

    @GetMapping("/{category}")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponse> getTransactions(@PathVariable Category category){
        return listTransactionsByCategoryUseCase.execute(category)
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @PostMapping(value = "/ai/openapi-transcription", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mp3")
    public ResponseEntity<Resource> createPrompt(
            @RequestParam("fileAudio") MultipartFile multipartFile,
            @RequestParam(name = "saveOnServer", required = false) boolean saveOnServer,
            @RequestHeader(value = "X-Source-Origin", defaultValue = "unknown-integration") String sourceOrigin) throws IOException {

        log.info("Request Param fileAudioName: [{}], SourceOrigin: [{}]", multipartFile.getName(), sourceOrigin);

        TransactionContext.set(sourceOrigin, multipartFile.getOriginalFilename(), "anonymous");

        try {
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

            var audioTranscriptionResponse = openAiAudioTranscriptionModel
                    .call(audioTranscriptionPrompt);

            var outputUserMessagePrompt = audioTranscriptionResponse.getResult().getOutput();

            log.info("OutputUserMessagePrompt: [{}]", outputUserMessagePrompt);

            // Processa a transcrição com a IA utilizando as tools configuradas no Controller
            var aiMessageResponse = chatClient.prompt()
                    .user(outputUserMessagePrompt)
                    .call()
                    .content();

            log.info("AI Response after Tool(Persist or List) Calling: [{}]", aiMessageResponse);

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
            byte[] audio = speechModel.call(aiMessageResponse);

            if(saveOnServer){
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
            }

            var byteArrayResource = new ByteArrayResource(audio);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment()
                                    .filename("AUDIO_output_"+now()+".mp3")
                                    .build().toString())
                    .body(byteArrayResource);
        } finally {
            TransactionContext.clear();
        }

    }
}
