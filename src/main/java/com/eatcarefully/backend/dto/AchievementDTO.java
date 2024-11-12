package com.eatcarefully.backend.dto;

import com.eatcarefully.backend.model.achievement.AchievementLevel;


public record AchievementDTO(
        String achievementName,
        AchievementLevel level
) {
}
