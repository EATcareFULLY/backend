package com.eatcarefully.backend.service.external;

import com.eatcarefully.backend.dto.RecommendationRequestDTO;
import com.eatcarefully.backend.service.PurchaseService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@AllArgsConstructor
public class RecommendationSystemClient implements IRecommendationSystemClient{


    private final String url = "url";
    private final WebClient webClient = WebClient.create();
    private final Duration TIMEOUT_IN_SECONDS  = Duration.ofSeconds(10);




    @Override
    public Mono<String> submitProductsForRecommendation(RecommendationRequestDTO dto) {
        return null;
    }

    @Override
    public Mono<JsonNode> getRecommendationResults(String recommendationId) {
        return null;
    }
}
