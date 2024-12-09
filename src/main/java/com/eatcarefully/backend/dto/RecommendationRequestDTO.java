package com.eatcarefully.backend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RecommendationRequestDTO {


    private String product_code;
    private int limit;
    private List<UserPreferenceDTO> user_preferences;

    public RecommendationRequestDTO(String productBarcode, int limit, List<UserPreferenceDTO> preferences) {
        this.product_code = productBarcode;
        this.limit = limit;
        this.user_preferences = preferences != null
                ? new ArrayList<>(preferences)
                : new ArrayList<>();
    }

}
