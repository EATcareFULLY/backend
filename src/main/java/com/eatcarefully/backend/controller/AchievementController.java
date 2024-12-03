package com.eatcarefully.backend.controller;

import com.eatcarefully.backend.dto.AchievementDTO;
import com.eatcarefully.backend.model.achievement.AchievementLevel;
import com.eatcarefully.backend.service.AchievementService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/achievements")
@AllArgsConstructor
public class AchievementController {

    private AchievementService achievementService;

    @GetMapping("/all")
    public ResponseEntity<List<AchievementDTO>> getUserAchievements(@AuthenticationPrincipal Jwt jwt) {
        List<AchievementDTO> userAchievements = achievementService.getUserAchievements(jwt);
        return ResponseEntity.ok(userAchievements);
    }

    private List<AchievementDTO> randomizeAchievementLevels(List<AchievementDTO> achievements) {
        List<AchievementDTO> achievementsToReturn = new ArrayList<>();
        for (int i = 0; i < achievements.size(); i++) {
            AchievementDTO achievement = achievements.get(i);
            if(i % 4 == 0) {
                achievementsToReturn.add(new AchievementDTO(achievement.achievementName(), achievement.achievementDescription(), AchievementLevel.NONE));
            } else if (i % 3 == 0) {
                achievementsToReturn.add(new AchievementDTO(achievement.achievementName(), achievement.achievementDescription(), AchievementLevel.BRONZE));
            } else if (i % 2 == 0) {
                achievementsToReturn.add(new AchievementDTO(achievement.achievementName(), achievement.achievementDescription(), AchievementLevel.SILVER));
            } else {
                achievementsToReturn.add(new AchievementDTO(achievement.achievementName(), achievement.achievementDescription(), AchievementLevel.GOLD));
            }
        }
        return achievementsToReturn;
    }
}
