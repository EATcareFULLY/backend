package com.eatcarefully.backend.repository;

import com.eatcarefully.backend.model.achievement.AchievementDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementDefinitionRepository extends JpaRepository<AchievementDefinition, Long> {

}
