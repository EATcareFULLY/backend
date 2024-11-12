package com.eatcarefully.backend.controller;

import com.eatcarefully.backend.dto.AchievementDTO;
import com.eatcarefully.backend.service.AchievementService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/achievements")
@AllArgsConstructor
public class AchievementController {

    private AchievementService achievementService;

    @GetMapping("/all")
    public ResponseEntity<List<AchievementDTO>> getUserAchievements(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(achievementService.getUserAchievements(jwt));
    }
}
