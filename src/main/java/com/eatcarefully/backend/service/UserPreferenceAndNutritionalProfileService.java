package com.eatcarefully.backend.service;

import com.eatcarefully.backend.dto.NutritionalThresholdsDTO;
import com.eatcarefully.backend.model.UserNutritionalProfile;
import com.eatcarefully.backend.repository.UserNutritionalProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserPreferenceAndNutritionalProfileService {


    private UserNutritionalProfileRepository userNutritionalProfileRepository;


    public Boolean userProfileExists(String username){
        return userNutritionalProfileRepository.existsById(username);
    }


    public UserNutritionalProfile createNutritionalProfile(String username, NutritionalThresholdsDTO dto){

        // check if already exists
        if( userNutritionalProfileRepository.existsById(username))
            throw new IllegalArgumentException("Such user profile already exists");

        UserNutritionalProfile profile = new UserNutritionalProfile(
                username,
                dto.getFatThreshold(),
                dto.getProteinThreshold(),
                dto.getCarbohydratesThreshold(),
                dto.getCaloriesThreshold()
        );

        return userNutritionalProfileRepository.save(profile);

    }


    public UserNutritionalProfile getNutritionalProfile(String username){

        return userNutritionalProfileRepository.findById(username).orElse( null);

    }


    public UserNutritionalProfile updateNutritionalProfileThresholds(String username, NutritionalThresholdsDTO dto){

        Optional<UserNutritionalProfile> tempProfile = userNutritionalProfileRepository.findById(username);

        if( tempProfile.isEmpty())
            throw new NoSuchElementException("No profile with this username");

        UserNutritionalProfile profile = tempProfile.get();

        profile.setFatThreshold(dto.getFatThreshold());
        profile.setProteinThreshold(dto.getProteinThreshold());
        profile.setCarbohydratesThreshold(dto.getCarbohydratesThreshold());
        profile.setCaloriesThreshold(dto.getCaloriesThreshold());

        return userNutritionalProfileRepository.save(profile);

    }





}