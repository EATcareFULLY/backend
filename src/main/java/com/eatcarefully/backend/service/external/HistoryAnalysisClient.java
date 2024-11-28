package com.eatcarefully.backend.service.external;

import com.eatcarefully.backend.dto.HistoryAnalysisProductDTO;
import com.eatcarefully.backend.dto.HistoryAnalysisRequestDTO;
import com.eatcarefully.backend.exceptions.DataNotFoundException;
import com.eatcarefully.backend.exceptions.ModelValidationException;
import com.eatcarefully.backend.exceptions.ServiceUnavailableException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.ValidationException;
import org.json.JSONObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;


@Service
public class HistoryAnalysisClient implements IHistoryAnalysisClient{


    private final String url = "http://localhost:8000/analyze/";
    private final WebClient webClient = WebClient.create();
    private final Duration TIMEOUT_IN_SECONDS  = Duration.ofSeconds(10);



    @Override
    public Mono<ResponseEntity<ByteArrayResource>> submitProductsForHistoryAnalysis(HistoryAnalysisRequestDTO dto) {



            return webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .retrieve()

                    .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, response ->
                            response.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new ValidationException("Validation error: " + errorBody)))
                    )
                    .bodyToMono(byte[].class) // Directly get the response body as byte[]
                    .flatMap(pdfContent -> {
                        if (pdfContent == null || pdfContent.length == 0) {
                            return Mono.error(new DataNotFoundException("PDF content is missing or empty"));
                        }

                        // Wrap PDF content in a ByteArrayResource with a filename
                        ByteArrayResource pdfResource = new ByteArrayResource(pdfContent) {
                            @Override
                            public String getFilename() {
                                return "history_analysis.pdf";
                            }
                        };

                        return Mono.just(ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_PDF)
                                .body(pdfResource));
                    })
                    .timeout(TIMEOUT_IN_SECONDS)
                    .onErrorMap(TimeoutException.class, e ->
                            new HttpTimeoutException("History analysis service took too long"))

                    .onErrorMap( WebClientRequestException.class, e ->
                            new ServiceUnavailableException("History analysis service unavailable"));



        }


}
