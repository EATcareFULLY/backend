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

    public LeaderboardDTO getLeaderboardForUser(String username) {

        List<LeaderboardRowDTO> topPositions = List.of(
                new LeaderboardRowDTO(1, "Ben Dover", 15000),
                new LeaderboardRowDTO(2, "Mike Coxlong", 14500),
                new LeaderboardRowDTO(3, "Hue G. Rection", 14000)
        );

        // Context around user position 15 (4 above, 4 below)
        List<LeaderboardRowDTO> userContext = List.of(
                new LeaderboardRowDTO(16, "Player16", 8500),
                new LeaderboardRowDTO(17, "Player17", 8300),
                new LeaderboardRowDTO(18, "Player18", 8100),
                new LeaderboardRowDTO(19, "Player19", 7900),
                new LeaderboardRowDTO(20, username, 7700),  // searched user
                new LeaderboardRowDTO(21, "Player21", 7500),
                new LeaderboardRowDTO(22, "Player22", 7300),
                new LeaderboardRowDTO(23, "Player23", 7100),
                new LeaderboardRowDTO(24, "Player24", 6900)
        );

        return new LeaderboardDTO(topPositions, userContext);
    }

    public LeaderboardDTO getEntireLeaderboard(Jwt jwt) {
        String username = jwtHelper.getUsernameFromToken(jwt);

        List<LeaderboardRowDTO> topPositions = List.of(
                new LeaderboardRowDTO(1, "Ben Dover", 15000),
                new LeaderboardRowDTO(2, "Mike Coxlong", 14500),
                new LeaderboardRowDTO(3, "Hue G. Rection", 14000)
        );

        List<LeaderboardRowDTO> theRest = List.of(
                new LeaderboardRowDTO(4, "Player4", 13500),
                new LeaderboardRowDTO(5, "Player5", 13000),
                new LeaderboardRowDTO(6, "Player6", 12500),
                new LeaderboardRowDTO(7, "Player7", 12000),
                new LeaderboardRowDTO(8, "Player8", 11500),
                new LeaderboardRowDTO(9, "Player9", 11000),
                new LeaderboardRowDTO(10, "Player10", 10500),
                new LeaderboardRowDTO(11, "Player11", 9500),
                new LeaderboardRowDTO(12, "Player12", 9300),
                new LeaderboardRowDTO(13, "Player13", 9100),
                new LeaderboardRowDTO(14, "Player14", 8900),
                new LeaderboardRowDTO(15, username, 8700),
                new LeaderboardRowDTO(16, "Player16", 8500),
                new LeaderboardRowDTO(17, "Player17", 8300),
                new LeaderboardRowDTO(18, "Player18", 8100),
                new LeaderboardRowDTO(19, "Player19", 7900),
                new LeaderboardRowDTO(20, "Player20", 7700),
                new LeaderboardRowDTO(21, "Player21", 7500),
                new LeaderboardRowDTO(22, "Player22", 7300),
                new LeaderboardRowDTO(23, "Player23", 7100),
                new LeaderboardRowDTO(24, "Player24", 6900)
        );

        return new LeaderboardDTO(topPositions, theRest);
    }
}
