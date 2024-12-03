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

    @Min(8)
    @Max(222)
    private int fat_threshold;

    @Min(6)
    @Max(500)
    private int protein_threshold;

    @Min(50)
    @Max(875)
    private int carbon_threshold;

    @Min(500)
    @Max(5000)
    private int calorie_threshold;





}
