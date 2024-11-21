package com.eatcarefully.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@org.hibernate.annotations.Check(
        constraints = "fat_threshold BETWEEN 0 AND 100 AND " +
                "protein_threshold BETWEEN 0 AND 300 AND " +
                "carbohydrates_threshold BETWEEN 0 AND 400 AND " +
                "calories_threshold BETWEEN 0 AND 2000")

public class UserNutritionalProfile {

    @Id
    private String username;
    private int fatThreshold;
    private int proteinThreshold;
    private int carbohydratesThreshold;
    private int caloriesThreshold;


}
