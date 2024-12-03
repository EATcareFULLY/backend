package com.eatcarefully.backend.repository;

import com.eatcarefully.backend.model.achievement.AchievementProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<AchievementProgress, Long> {

    List<AchievementProgress> findAllByUsername(String username);

    Optional<AchievementProgress> findByUsernameAndAchievementDefinition_Id(String username, Long id);
}
