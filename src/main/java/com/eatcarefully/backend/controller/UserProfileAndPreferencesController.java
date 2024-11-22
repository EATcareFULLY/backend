package com.eatcarefully.backend.controller;


import com.eatcarefully.backend.dto.UserThresholdAndPreferencesDTO;
import com.eatcarefully.backend.helper.JwtHelper;
import com.eatcarefully.backend.model.UserNutritionalProfile;
import com.eatcarefully.backend.service.UserPreferenceAndNutritionalProfileService;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-profile")
@AllArgsConstructor
public class UserProfileAndPreferencesController {


    private UserPreferenceAndNutritionalProfileService userService;
    private JwtHelper jwtHelper;



    @GetMapping()
    public ResponseEntity<UserThresholdAndPreferencesDTO> getUserThresholdsAndPreferences(@AuthenticationPrincipal Jwt jwt){

        String username = jwtHelper.getUsernameFromToken(jwt);
        try{
            return ResponseEntity.ok(userService.getOrCreateThresholdsAndPreferences(username));

        }
        catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }

    }



    @PostMapping("/update")
    public ResponseEntity<UserThresholdAndPreferencesDTO> updateThresholdsAndPreferences(@AuthenticationPrincipal Jwt jwt ,
                                                                                          @RequestBody UserThresholdAndPreferencesDTO dto) {

        try {

            // check if profile exist
            String username = jwtHelper.getUsernameFromToken(jwt);
            return ResponseEntity.ok(userService.updateUserPreferencesAndThresholds(username, dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();

        }
    }


}
