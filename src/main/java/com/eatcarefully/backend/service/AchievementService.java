package com.eatcarefully.backend.service;

import com.eatcarefully.backend.dto.AchievementDTO;
import com.eatcarefully.backend.helper.JwtHelper;
import com.eatcarefully.backend.model.achievement.AchievementDefinition;
import com.eatcarefully.backend.model.achievement.AchievementLevel;
import com.eatcarefully.backend.model.achievement.AchievementProgress;
import com.eatcarefully.backend.repository.AchievementDefinitionRepository;
import com.eatcarefully.backend.repository.AchievementRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AchievementService {

    private final JwtHelper jwtHelper;
    private final AchievementRepository progressRepository;
    private final AchievementDefinitionRepository definitionRepository;

    private final List<AchievementDefinition> achievementDefinitions;

    public List<AchievementDTO> getUserAchievements(Jwt jwt){
        String username = jwtHelper.getUsernameFromToken(jwt);

        List<AchievementProgress> userAchievements = progressRepository.findAllByUsername(username);

        return userAchievements.stream().map(AchievementProgress::toDTO).toList();
    }


    public Optional<AchievementDTO> incrementAchievementProgress(String username, Long achievementId, int increment){
        Optional<AchievementProgress> progressOptional = progressRepository.findByUsernameAndAchievementDefinition_Id(username, achievementId);
        if(progressOptional.isEmpty()){
            initializeAchievementProgresses(username);
            return Optional.empty();
        } else {
            AchievementProgress progress = progressOptional.get();
            progress.setCurrentCount(progress.getCurrentCount() + increment);

            AchievementDefinition definition = achievementDefinitions.stream().filter(
                    def -> Objects.equals(def.getId(), achievementId)
            ).findFirst().get();

            AchievementLevel levelAfterIncrement;
            if (progress.getCurrentCount() >= definition.getThresholdGold()) {
                levelAfterIncrement = AchievementLevel.GOLD;
            } else if (progress.getCurrentCount() >= definition.getThresholdSilver()) {
                levelAfterIncrement = AchievementLevel.SILVER;
            } else if (progress.getCurrentCount() >= definition.getThresholdBronze()) {
                levelAfterIncrement = AchievementLevel.BRONZE;
            } else {
                return Optional.empty(); // no level achieved yet
            }

            // only update if the level has changed
            if (levelAfterIncrement != progress.getCurrentLevel()) {
                progress.setCurrentLevel(levelAfterIncrement);
                progressRepository.save(progress);
                return Optional.of(new AchievementDTO(definition.getName(), levelAfterIncrement));
            } else {
                return Optional.empty();
            }
        }
    }

    // TODO: use it in the purchase methods

    private void initializeAchievementProgresses(String username) {
        for (AchievementDefinition definition : achievementDefinitions) {
            progressRepository.save(
                    new AchievementProgress(null, username, definition, 0, AchievementLevel.NONE)
            );
        }
    }
}
