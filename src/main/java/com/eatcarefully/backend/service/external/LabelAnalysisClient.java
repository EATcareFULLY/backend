package com.eatcarefully.backend.service.external;


import com.eatcarefully.backend.exceptions.ModelValidationException;
import com.eatcarefully.backend.helper.ImageHelper;
import com.eatcarefully.backend.service.OCRService;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.handler.timeout.ReadTimeoutException;
import java.util.concurrent.TimeoutException;
import jakarta.xml.bind.ValidationException;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import com.eatcarefully.backend.exceptions.ServiceUnavailableException;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Map;

@Service
@AllArgsConstructor

public class LabelAnalysisClient implements ILabelAnalysisClient{


    private final String url = "http://localhost/service/analyze-label";
    private final WebClient webClient = WebClient.create();
    private final int MAX_LENGTH = 3000;
    private final Duration TIMEOUT_IN_SECONDS  = Duration.ofSeconds(10);


    public Mono<JsonNode> submitLabelForAnalysis(String labelText) {

        if(! isLabelTextValid(labelText))
            return Mono.error(new ModelValidationException("Label text is not valid. (empty, blank or too long)"));


        JSONObject json = new JSONObject();
        json.put("label_text", labelText);

        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json.toMap())
                .retrieve()

                .onStatus(HttpStatus.UNPROCESSABLE_ENTITY::equals, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new IllegalArgumentException("Validation error: " + errorBody)))
                )
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new ValidationException("Validation error: " + errorBody)))
                )
                .bodyToMono(JsonNode.class)

                .timeout(TIMEOUT_IN_SECONDS)

                .onErrorMap(TimeoutException.class, e ->
                    new HttpTimeoutException("Label analysis service took too long"))

                .onErrorMap( WebClientRequestException.class, e ->
                        new ServiceUnavailableException("Label analysis service unavailable"));




    }

    public Boolean isLabelTextValid(String labelText){

        return ILabelAnalysisClient.super.isLabelTextValid(labelText) && labelText.length() <= MAX_LENGTH;
    }











}
