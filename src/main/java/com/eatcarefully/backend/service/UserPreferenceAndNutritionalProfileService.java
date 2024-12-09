package com.eatcarefully.backend.service;

import com.eatcarefully.backend.dto.NutritionalThresholdsDTO;
import com.eatcarefully.backend.dto.UserPreferenceDTO;
import com.eatcarefully.backend.dto.UserThresholdAndPreferencesDTO;
import com.eatcarefully.backend.model.PreferenceName;
import com.eatcarefully.backend.model.UserNutritionalProfile;
import com.eatcarefully.backend.model.UserPreference;
import com.eatcarefully.backend.model.UserPreferenceCompositeKey;
import com.eatcarefully.backend.repository.PreferenceNameRepository;
import com.eatcarefully.backend.repository.UserNutritionalProfileRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserPreferenceAndNutritionalProfileService {


    private UserNutritionalProfileRepository userNutritionalProfileRepository;
    private PreferenceNameRepository preferenceNameRepository;


    public Boolean userProfileExists(String username){
        return userNutritionalProfileRepository.existsById(username);
    }


    public UserNutritionalProfile createNutritionalProfile(String username){

        // check if already exists
        if( userNutritionalProfileRepository.existsById(username))
            throw new IllegalArgumentException("Such user profile already exists");

        UserNutritionalProfile profile = new UserNutritionalProfile(username);
        return userNutritionalProfileRepository.save(profile);

    }


    public UserThresholdAndPreferencesDTO updateUserPreferencesAndThresholds(String username, UserThresholdAndPreferencesDTO dto ){

        //check if user exists
        UserNutritionalProfile profile = userNutritionalProfileRepository.findById(username).orElse(null);

        if( profile == null){

            profile = createNutritionalProfile(username);
            updateNutritionalProfileThresholds(profile, dto.getThresholds());

        }
        else {

            updateNutritionalProfileThresholds(profile, dto.getThresholds());
        }


            if (!dto.getPreferences().isEmpty())
                handleUserPreferencesUpdate(profile, dto.getPreferences());


        return profile.userThresholdAndPreferencesDTO();

    }


    public void updateNutritionalProfileThresholds(UserNutritionalProfile profile, NutritionalThresholdsDTO dto){

        profile.setFatThreshold(dto.getFat_threshold());
        profile.setProteinThreshold(dto.getProtein_threshold());
        profile.setCarbohydratesThreshold(dto.getCarbon_threshold());
        profile.setCaloriesThreshold(dto.getCalorie_threshold());

        userNutritionalProfileRepository.save(profile);

    }

    private void handleUserPreferencesUpdate(UserNutritionalProfile profile, List<UserPreferenceDTO> list){

        for(UserPreferenceDTO dto: list){

            PreferenceName prefName = preferenceNameRepository.findByName(dto.getName()).orElse(null);

            if( prefName == null)
                throw new IllegalArgumentException("No such preference name: " + dto.getName());

            UserPreference profilePref = profile.getUserPreferenceByName(dto.getName());

            if (dto.getStatus() == 0) {

                //remove if exists


                if(profilePref != null)
                    profile.removeUserPreference(profilePref);

            }
            else{

                if(profilePref != null)

                    profilePref.setWanted(dto.getStatus());

                else{

                    UserPreferenceCompositeKey key = new UserPreferenceCompositeKey(profile.getUsername(), prefName.getId());
                    UserPreference newPref = new UserPreference();
                    newPref.setId(key);
                    newPref.setPreferenceName(prefName);
                    newPref.setWanted(dto.getStatus());

                    profile.addUserPreference(newPref);

                }


            }

        }
        userNutritionalProfileRepository.save(profile);

    }


    public List<UserPreferenceDTO> getUserPreferencesList(String username){

        if( ! userProfileExists(username)) {
            createNutritionalProfile(username);
        }

        UserNutritionalProfile profile = userNutritionalProfileRepository.findById(username).orElse(null);

        return (profile != null) ? profile.getListOfUserPreferenceDTO() : null;

    }


    public NutritionalThresholdsDTO getUserThresholds(String username){

        if( ! userProfileExists(username)) {
            createNutritionalProfile(username);
        }

        UserNutritionalProfile profile = userNutritionalProfileRepository.findById(username).orElse(null);

        return (profile != null) ? profile.getNutritionalThresholdsDTO() : null;

    }


    public UserThresholdAndPreferencesDTO getOrCreateThresholdsAndPreferences(String username){

        if( ! userProfileExists(username)) {
            createNutritionalProfile(username);
        }

        UserThresholdAndPreferencesDTO dto = new UserThresholdAndPreferencesDTO(getUserThresholds(username), getUserPreferencesList(username));
        log.info(dto.toString());
        return dto;


    }










}