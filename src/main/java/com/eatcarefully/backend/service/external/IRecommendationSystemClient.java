package com.eatcarefully.backend.service.external;

import com.eatcarefully.backend.dto.RecommendationRequestDTO;
import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

public interface IRecommendationSystemClient {

    public Mono<JsonNode> submitProductsForRecommendation(RecommendationRequestDTO dto);


}
