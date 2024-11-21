package com.eatcarefully.backend.service.external;

import com.eatcarefully.backend.dto.HistoryAnalysisProductDTO;
import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IHistoryAnalysisClient {

    public Mono<JsonNode> submitProductsForHistoryAnalysis(List<HistoryAnalysisProductDTO> products);

}
