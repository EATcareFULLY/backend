package com.eatcarefully.backend.dto;

import java.util.List;

public record PurchaseResponseDTO(
        List<AchievementDTO> unlockedAchievements,
        String errorMessage
) {
}
