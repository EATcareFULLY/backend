package com.eatcarefully.backend.service;

import com.eatcarefully.backend.model.leaderboard.LeaderboardPosition;
import com.eatcarefully.backend.repository.LeaderboardPositionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LeaderboardDataInitializer {
    private final LeaderboardPositionRepository leaderboardPositionRepository;

    @Value("${app.init-mock-leaderboard}")
    private boolean shouldInitLeaderboard;

    @PostConstruct
    public void initLeaderboardData() {
        Optional<LeaderboardPosition> mockLeaderboardEntry = leaderboardPositionRepository.findByUsername("GrandMaster123");
        boolean mockDataAlreadyPresent = mockLeaderboardEntry.isPresent();

        if (!shouldInitLeaderboard || mockDataAlreadyPresent) {
            return;
        }

        List<LeaderboardPosition> initialData = List.of(
                new LeaderboardPosition(null, "GrandMaster123", 300),
                new LeaderboardPosition(null, "ProGamer99", 290),
                new LeaderboardPosition(null, "ElitePlayer", 280),
                new LeaderboardPosition(null, "HealthyEater", 270),
                new LeaderboardPosition(null, "NutriScore_King", 260),
                new LeaderboardPosition(null, "VeggieQueen", 250),
                new LeaderboardPosition(null, "OrganicLover", 240),
                new LeaderboardPosition(null, "HealthyChoices", 230),
                new LeaderboardPosition(null, "NutritionPro", 220),
                new LeaderboardPosition(null, "FoodWizard", 210),
                new LeaderboardPosition(null, "EcoShopper", 200),
                new LeaderboardPosition(null, "HealthyLife", 190),
                new LeaderboardPosition(null, "GreenChoice", 180),
                new LeaderboardPosition(null, "WiseConsumer", 170),
                new LeaderboardPosition(null, "FoodExplorer", 160),
                new LeaderboardPosition(null, "SmartShopper", 150),
                new LeaderboardPosition(null, "HealthyMind", 140),
                new LeaderboardPosition(null, "EcoWarrior", 130),
                new LeaderboardPosition(null, "GoodChoice", 120),
                new LeaderboardPosition(null, "FreshStart", 110)
        );

        leaderboardPositionRepository.saveAll(initialData);
    }
}