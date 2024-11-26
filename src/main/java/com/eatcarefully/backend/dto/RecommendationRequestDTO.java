package com.eatcarefully.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RecommendationRequestDTO {


    private String product_code;
    private int limit= 3;
    private List<UserPreferenceDTO> user_preferences;


}
