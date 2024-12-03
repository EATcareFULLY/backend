package com.eatcarefully.backend.controller;

import com.eatcarefully.backend.dto.LeaderboardDTO;
import com.eatcarefully.backend.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    //mock data for now TODO: implement actual leaderboard
    @GetMapping("/me")
    public ResponseEntity<LeaderboardDTO> getLeaderboardForCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(leaderboardService.getLeaderboardForCurrentUser(jwt));
    }

    @GetMapping("/{username}")
    public ResponseEntity<LeaderboardDTO> getLeaderboardForSearchedUser(@PathVariable String username) {
        LeaderboardDTO leaderboardForSearchedUser = leaderboardService.getLeaderboardForSearchedUser(username);
        if (Objects.isNull(leaderboardForSearchedUser)) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(leaderboardForSearchedUser);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<LeaderboardDTO> getEntireLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getEntireLeaderboard());
    }
}
