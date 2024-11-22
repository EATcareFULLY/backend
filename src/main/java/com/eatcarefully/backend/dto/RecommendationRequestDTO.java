package com.eatcarefully.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RecommendationRequestDTO {


    private List<String> barcodes;
    private List<UserPreferenceDTO> preferences;


}
