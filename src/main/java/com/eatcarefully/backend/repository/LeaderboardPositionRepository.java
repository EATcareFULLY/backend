package com.eatcarefully.backend.repository;

import com.eatcarefully.backend.model.leaderboard.LeaderboardPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaderboardPositionRepository extends JpaRepository<LeaderboardPosition, Long> {
    Optional<LeaderboardPosition> findByUsername(String username);
}


