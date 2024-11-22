package com.eatcarefully.backend.model;

import com.eatcarefully.backend.dto.NutritionalThresholdsDTO;
import com.eatcarefully.backend.dto.UserPreferenceDTO;
import com.eatcarefully.backend.dto.UserThresholdAndPreferencesDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@org.hibernate.annotations.Check(
        constraints = "fat_threshold BETWEEN 0 AND 5000 AND " +
                "protein_threshold BETWEEN 0 AND 5000 AND " +
                "carbohydrates_threshold BETWEEN 0 AND 5000 AND " +
                "calories_threshold BETWEEN 0 AND 5000")

public class UserNutritionalProfile {

    @Id
    private String username;
    private int fatThreshold;
    private int proteinThreshold;
    private int carbohydratesThreshold;
    private int caloriesThreshold;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPreference> preferences = new ArrayList<>();

    public UserNutritionalProfile(String username, int fatThreshold, int proteinThreshold, int carbohydratesThreshold, int caloriesThreshold) {
        this.username = username;
        this.fatThreshold = fatThreshold;
        this.proteinThreshold = proteinThreshold;
        this.carbohydratesThreshold = carbohydratesThreshold;
        this. caloriesThreshold = caloriesThreshold;
    }

    public void addUserPreference(UserPreference preference){

        if( preference != null){
            preference.setUserProfile(this);
            this.preferences.add(preference);
        }

    }

    public void removeUserPreference(UserPreference preference){

        if(preference != null)
            this.preferences.remove(preference);

    }


    public boolean containsPreferenceWithName(String name){

        if(name.isEmpty() || this.preferences.isEmpty())
            return false;

        return this.preferences.stream()
                .anyMatch( pref -> pref.getPreferenceName().getName().equals(name));

    }


    public UserPreference getUserPreferenceByName(String name){

        return this.preferences
                .stream()
                .filter(pref -> pref.getPreferenceName().getName().equals(name))
                .findFirst()
                .orElse(null);

    }

    public NutritionalThresholdsDTO getNutritionalThresholdsDTO(){

        return new NutritionalThresholdsDTO(this.fatThreshold, this.proteinThreshold, this.carbohydratesThreshold, this.caloriesThreshold);

    }

    public List<UserPreferenceDTO> getListOfUserPreferenceDTO(){

        return this.preferences.stream().map(UserPreference::toDTO).toList();
    }


    public UserThresholdAndPreferencesDTO userThresholdAndPreferencesDTO(){

        return new UserThresholdAndPreferencesDTO(getNutritionalThresholdsDTO(), getListOfUserPreferenceDTO());

    }



}
