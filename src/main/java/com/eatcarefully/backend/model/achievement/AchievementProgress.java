package com.eatcarefully.backend.model.achievement;

import com.eatcarefully.backend.dto.AchievementDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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
                achievementDefinition.getDescription(),
                currentLevel
                );
    }
}

