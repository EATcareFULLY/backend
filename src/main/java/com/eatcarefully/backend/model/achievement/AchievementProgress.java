package com.eatcarefully.backend.model.achievement;

import com.eatcarefully.backend.dto.AchievementDTO;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class AchievementProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @ManyToOne
    private AchievementDefinition achievementDefinition;

    private int currentCount;

    private AchievementLevel currentLevel;


    public AchievementDTO toDTO() {
        return new AchievementDTO(
                achievementDefinition.getName(),
                currentLevel
                );
    }
}

