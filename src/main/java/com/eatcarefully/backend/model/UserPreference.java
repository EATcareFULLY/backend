package com.eatcarefully.backend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class UserPreference {

    @EmbeddedId
    private UserPreferenceCompositeKey id;

    @MapsId("username")
    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private UserNutritionalProfile userProfile;

    @MapsId("preferenceNameId")
    @ManyToOne
    @JoinColumn(name = "preferenceNameId", referencedColumnName = "id")
    private PreferenceName preferenceName;

    private int wanted;




}
