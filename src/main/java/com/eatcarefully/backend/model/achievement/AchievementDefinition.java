package com.eatcarefully.backend.model.achievement;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class AchievementDefinition {

    @Id
    private Long id;

    private String name;

    private int bronzeThreshold;
    private int silverThreshold;
    private int goldThreshold;
}
