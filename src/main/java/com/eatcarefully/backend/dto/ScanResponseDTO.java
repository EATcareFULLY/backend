package com.eatcarefully.backend.dto;

import java.util.List;

public record ScanResponseDTO(
        String id,
        String name,
        String score,
        String brand,
        String imageURL,
        List<TagDTO> tags,
        List<AllergenDTO> allergens,
        List<IngredientDTO> ingredients,

        List<AchievementDTO> newAchievements
) {
}
