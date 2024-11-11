package com.eatcarefully.backend.dto;

import com.eatcarefully.backend.model.achievement.AchievementLevel;
import lombok.Data;


public record AchievementDTO(
        String achievementName,
        AchievementLevel level
) {
}
