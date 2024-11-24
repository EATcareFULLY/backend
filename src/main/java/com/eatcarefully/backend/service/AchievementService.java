package com.eatcarefully.backend.service;

import com.eatcarefully.backend.dto.AchievementDTO;
import com.eatcarefully.backend.helper.JwtHelper;
import com.eatcarefully.backend.model.Product;
import com.eatcarefully.backend.model.achievement.AchievementDefinition;
import com.eatcarefully.backend.model.achievement.AchievementLevel;
import com.eatcarefully.backend.model.achievement.AchievementProgress;
import com.eatcarefully.backend.repository.AchievementDefinitionRepository;
import com.eatcarefully.backend.repository.AchievementRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AchievementService {

    private final JwtHelper jwtHelper;
    private final AchievementRepository progressRepository;
    private final AchievementDefinitionRepository definitionRepository;

    private List<AchievementDefinition> achievementDefinitions;

    @PostConstruct
    private void initializeAchievementDefinitions() {
        achievementDefinitions = definitionRepository.findAll();
    }

    public List<AchievementDTO> getUserAchievements(Jwt jwt) {
        String username = jwtHelper.getUsernameFromToken(jwt);

        createProgressesIfNotExists(username);
        List<AchievementProgress> userAchievements = progressRepository.findAllByUsername(username);

        return userAchievements.stream().map(AchievementProgress::toDTO).toList();
    }

    public List<AchievementDTO> verifyPurchaseAchievements(String username, Product product) {
        List<AchievementDTO> unlockedAchievements = new ArrayList<>();

        Optional<AchievementDTO> unlockedAchievementOptional = incrementAchievementProgress(username, 1L, 1);
        unlockedAchievementOptional.ifPresent(unlockedAchievements::add);

        for (AchievementDefinition achievementDefinition : achievementDefinitions) {
            switch (achievementDefinition.getAchievementType()) {
                case PRODUCT_CATEGORY:  //TODO: handle categories in different languages
                    if (product.getCategories().stream().anyMatch(category -> category.getName().equals(achievementDefinition.getAchievementParameter()))) {
                        Optional<AchievementDTO> unlockedAchievement = incrementAchievementProgress(username, achievementDefinition.getId(), 1);
                        unlockedAchievement.ifPresent(unlockedAchievements::add);
                    }
                    break;
                case PRODUCT_NAME:
                    // case insensitive search
                    String productName = product.getName().toLowerCase();
                    String searchedPhrase = achievementDefinition.getAchievementParameter().toLowerCase();
                    if (productName.contains(searchedPhrase)) {
                        Optional<AchievementDTO> unlockedAchievement = incrementAchievementProgress(username, achievementDefinition.getId(), 1);
                        unlockedAchievement.ifPresent(unlockedAchievements::add);
                    }
                    break;
                case NUTRI_SCORE:
                    // case insensitive search
                    String productNutriScore = product.getScore().toLowerCase();
                    String achievementNutriScore = achievementDefinition.getAchievementParameter().toLowerCase();
                    if (productNutriScore.equals(achievementNutriScore)) {
                        Optional<AchievementDTO> unlockedAchievement = incrementAchievementProgress(username, achievementDefinition.getId(), 1);
                        unlockedAchievement.ifPresent(unlockedAchievements::add);
                    }
                    break;
                case NOVA_SCORE:
                    if (product.getNovaGroup().equals(achievementDefinition.getAchievementParameter())) {
                        Optional<AchievementDTO> unlockedAchievement = incrementAchievementProgress(username, achievementDefinition.getId(), 1);
                        unlockedAchievement.ifPresent(unlockedAchievements::add);
                    }
                    break;
                default:
                    break;
            }
        }

        return unlockedAchievements;
    }

    public Optional<AchievementDTO> incrementAchievementProgress(String username, Long achievementId, int increment) {
        createProgressesIfNotExists(username);
        Optional<AchievementProgress> progressOptional = progressRepository.findByUsernameAndAchievementDefinition_Id(username, achievementId);

        if (progressOptional.isEmpty()) {
            initializeAchievementProgresses(username);
            return Optional.empty();
        } else {
            AchievementProgress progress = progressOptional.get();
            progress.setCurrentCount(progress.getCurrentCount() + increment);

            AchievementDefinition definition = achievementDefinitions.stream().filter(
                    def -> Objects.equals(def.getId(), achievementId)
            ).findFirst().orElseThrow();

            AchievementLevel levelAfterIncrement;
            if (progress.getCurrentCount() >= definition.getThresholdGold()) {
                levelAfterIncrement = AchievementLevel.GOLD;
            } else if (progress.getCurrentCount() >= definition.getThresholdSilver()) {
                levelAfterIncrement = AchievementLevel.SILVER;
            } else if (progress.getCurrentCount() >= definition.getThresholdBronze()) {
                levelAfterIncrement = AchievementLevel.BRONZE;
            } else {
                progressRepository.save(progress);
                return Optional.empty(); // no level achieved yet
            }

            // only update if the level has changed
            if (levelAfterIncrement != progress.getCurrentLevel()) {
                progress.setCurrentLevel(levelAfterIncrement);
                progressRepository.save(progress);
                return Optional.of(new AchievementDTO(definition.getName(), "Achievement Description Placeholder", levelAfterIncrement));
            } else {
                progressRepository.save(progress);
                return Optional.empty();
            }
        }
    }

    private void createProgressesIfNotExists(String username) {
        if (progressRepository.findAllByUsername(username).isEmpty()) {
            initializeAchievementProgresses(username);
        }
    }

    private void initializeAchievementProgresses(String username) { // TODO: check whether every achievement is added
        for (AchievementDefinition definition : achievementDefinitions) {
            progressRepository.save(
                    new AchievementProgress(null, username, definition, 0, AchievementLevel.NONE)
            );
        }
    }


//    public List<AchievementDTO> verifyPurchaseAchievements(String username, Product purchasedProduct) {
//        List<String> categories = purchasedProduct.getCategories().stream().map(Category::getName).toList();
//
//        // ify z każdą kategorią
//    }
}
