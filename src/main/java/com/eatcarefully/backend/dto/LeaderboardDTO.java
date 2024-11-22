package com.eatcarefully.backend.dto;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public record LeaderboardDTO (
    List<LeaderboardRowDTO> topPositions,  // top 3 users
    List<LeaderboardRowDTO> userContext    // nearby users
) {
}
