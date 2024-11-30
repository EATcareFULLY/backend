package com.eatcarefully.backend.dto;

public record IngredientDTO(
        String name,
        String description,
        Float content
) {
}
