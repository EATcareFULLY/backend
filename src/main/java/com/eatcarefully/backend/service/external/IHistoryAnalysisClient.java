package com.eatcarefully.backend.service.external;

import com.eatcarefully.backend.dto.HistoryAnalysisRequestDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IHistoryAnalysisClient {

    public Mono<ResponseEntity<ByteArrayResource>> submitProductsForHistoryAnalysis(HistoryAnalysisRequestDTO dto);

}
