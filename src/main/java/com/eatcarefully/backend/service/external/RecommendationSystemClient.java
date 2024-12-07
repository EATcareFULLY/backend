package com.eatcarefully.backend.service.external;

import com.eatcarefully.backend.dto.RecommendationRequestDTO;
import com.eatcarefully.backend.exceptions.ServiceUnavailableException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Service
public class RecommendationSystemClient implements IRecommendationSystemClient {


    @Value("${app.recommendation-service.url}")
    private String url;
    private final WebClient webClient = WebClient.create();
    private final Duration TIMEOUT_IN_SECONDS = Duration.ofSeconds(15);


    @Override
    public Mono<JsonNode> submitProductsForRecommendation(RecommendationRequestDTO dto) {


        return webClient.post()
                .uri(url + dto.getProduct_code()/* + "/"*/)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
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
                        new HttpTimeoutException("Recommendation service took too long"))

                .onErrorMap(WebClientRequestException.class, e ->
                        new ServiceUnavailableException("Recommendation service unavailable"));


    }
}
