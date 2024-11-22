package com.eatcarefully.backend.service;

import com.eatcarefully.backend.dto.LeaderboardDTO;
import com.eatcarefully.backend.dto.LeaderboardRowDTO;
import com.eatcarefully.backend.helper.JwtHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final JwtHelper jwtHelper;
    public LeaderboardDTO getTestLeaderboard(Jwt jwt) {
        String username = jwtHelper.getUsernameFromToken(jwt);

        List<LeaderboardRowDTO> topPositions = List.of(
                new LeaderboardRowDTO(1, "Ben Dover", 15000),
                new LeaderboardRowDTO(2, "Mike Coxlong", 14500),
                new LeaderboardRowDTO(3, "Hue G. Rection", 14000)
        );

        // Context around position 15 (11-19)
        List<LeaderboardRowDTO> userContext = List.of(
                new LeaderboardRowDTO(11, "Player11", 9500),
                new LeaderboardRowDTO(12, "Player12", 9300),
                new LeaderboardRowDTO(13, "Player13", 9100),
                new LeaderboardRowDTO(14, "Player14", 8900),
                new LeaderboardRowDTO(15, username, 8700),  // Current user
                new LeaderboardRowDTO(16, "Player16", 8500),
                new LeaderboardRowDTO(17, "Player17", 8300),
                new LeaderboardRowDTO(18, "Player18", 8100),
                new LeaderboardRowDTO(19, "Player19", 7900)
        );

        return new LeaderboardDTO(topPositions, userContext);
    }
}
