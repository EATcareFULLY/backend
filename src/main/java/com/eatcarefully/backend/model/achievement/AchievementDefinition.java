package com.eatcarefully.backend.model.achievement;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "achievement_definition")
public class AchievementDefinition {

    @Id
    private Long id;

    private String name;

    private int thresholdBronze;
    private int thresholdSilver;
    private int thresholdGold;
}
