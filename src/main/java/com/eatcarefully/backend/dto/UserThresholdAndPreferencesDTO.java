package com.eatcarefully.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserThresholdAndPreferencesDTO {

    private NutritionalThresholdsDTO thresholds;
    private List<UserPreferenceDTO> preferences;

}
