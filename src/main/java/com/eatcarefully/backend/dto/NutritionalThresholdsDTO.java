package com.eatcarefully.backend.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NutritionalThresholdsDTO {

    @Min(0)
    @Max(100)
    private int fatThreshold;

    @Min(0)
    @Max(100)
    private int proteinThreshold;

    @Min(0)
    @Max(100)
    private int carbohydratesThreshold;

    @Min(0)
    @Max(100)
    private int caloriesThreshold;





}
