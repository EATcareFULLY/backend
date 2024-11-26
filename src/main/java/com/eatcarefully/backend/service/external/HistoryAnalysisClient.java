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
import java.util.List;
import java.util.concurrent.TimeoutException;


@Service
public class HistoryAnalysisClient implements IHistoryAnalysisClient{


    private final String url = "url";
    private final WebClient webClient = WebClient.create();
    private final Duration TIMEOUT_IN_SECONDS  = Duration.ofSeconds(10);



    @Override
    public Mono<ResponseEntity<MultiValueMap<String, Object>>> submitProductsForHistoryAnalysis(HistoryAnalysisRequestDTO dto) {



            return webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .retrieve()

                    .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, response ->
                            response.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new ValidationException("Validation error: " + errorBody)))
                    )
                    .toEntity(MultiValueMap.class)
                    .flatMap( responseEntity -> {

                        MultiValueMap<String, HttpEntity<?>> body = responseEntity.getBody();


                        // JSON
                        if (body == null) {
                            return Mono.error(new DataNotFoundException("Missing json in body"));
                        }

                        HttpEntity<?> jsonEntity = body.getFirst("json");
                        if (jsonEntity == null || jsonEntity.getBody() == null) {
                            return Mono.error(new DataNotFoundException("Json part of response is missing."));
                        }

                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonPart;
                        try {
                            jsonPart = objectMapper.readTree(jsonEntity.getBody().toString());
                        } catch (Exception e) {
                            return Mono.error(new IllegalArgumentException("Failed to parse JSON part of the response", e));
                        }

                        //PDF
                        HttpEntity<?> pdfEntity = body.getFirst("pdf");
                        if (pdfEntity == null || pdfEntity.getBody() == null) {
                            return Mono.error(new IllegalStateException("PDF part of response is missing."));
                        }

                        byte[] pdfContent = null;
                        if (pdfEntity.getBody() instanceof byte[]) {
                            pdfContent = (byte[]) pdfEntity.getBody();
                        } else {
                            return Mono.error(new IllegalArgumentException("Unexpected type for PDF part in response"));
                        }

                        // build response
                        MultiValueMap<String, Object> result = new LinkedMultiValueMap<>();
                        result.add("json", jsonPart);
                        result.add("pdf", new ByteArrayResource(pdfContent) {
                            @Override
                            public String getFilename() {
                                return "history_analysis.pdf";
                            }
                        });
                        return Mono.just(ResponseEntity.ok(result));


                    })
                    .timeout(TIMEOUT_IN_SECONDS)
                    .onErrorMap(TimeoutException.class, e ->
                            new HttpTimeoutException("History analysis service took too long"))

                    .onErrorMap( WebClientRequestException.class, e ->
                            new ServiceUnavailableException("History analysis service unavailable"));



        }


}
