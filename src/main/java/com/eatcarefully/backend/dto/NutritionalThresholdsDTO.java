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
    private int fat_threshold;

    @Min(0)
    @Max(100)
    private int protein_threshold;

    @Min(0)
    @Max(100)
    private int carbon_threshold;

    @Min(0)
    @Max(100)
    private int calorie_threshold;





}
