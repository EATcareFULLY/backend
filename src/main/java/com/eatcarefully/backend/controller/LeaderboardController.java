package com.eatcarefully.backend.controller;

import com.eatcarefully.backend.dto.LeaderboardDTO;
import com.eatcarefully.backend.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/me")
    public ResponseEntity<LeaderboardDTO> getTestLeaderboard(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(leaderboardService.getTestLeaderboard(jwt));   //mock data for now
    }
}
