package com.eatcarefully.backend.dto;

import java.util.List;

public record LeaderboardDTO (
    List<LeaderboardRowDTO> topPositions,  // top 3 users
    List<LeaderboardRowDTO> userContext,    // nearby users
    int userPosition,
    int totalPositions
) {
}
