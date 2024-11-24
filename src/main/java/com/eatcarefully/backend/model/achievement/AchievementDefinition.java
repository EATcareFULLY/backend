package com.eatcarefully.backend.model.achievement;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "achievement_definition")
public class AchievementDefinition {

    @Id
    private Long id;
    private String name;
    private String description;
    private int thresholdBronze;
    private int thresholdSilver;
    private int thresholdGold;

    @Enumerated(EnumType.STRING)
    private AchievementType achievementType;
    private String achievementParameter;
}
