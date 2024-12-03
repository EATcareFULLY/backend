package com.eatcarefully.backend.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

public interface ILabelAnalysisClient {


    public Mono<JsonNode> submitLabelForAnalysis(String labelText);


    default Boolean isLabelTextValid(String labelText){
        return labelText != null && ! labelText.isEmpty() && ! labelText.isBlank();

    }


}
