package com.eatcarefully.backend.service.external;

import com.eatcarefully.backend.dto.HistoryAnalysisProductDTO;
import com.eatcarefully.backend.dto.HistoryAnalysisRequestDTO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IHistoryAnalysisClient {

    public Mono<ResponseEntity<MultiValueMap<String, Object>>> submitProductsForHistoryAnalysis(HistoryAnalysisRequestDTO dto);

}
