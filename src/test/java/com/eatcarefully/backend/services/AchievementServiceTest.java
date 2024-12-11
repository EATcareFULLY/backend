package com.eatcarefully.backend.services;

import com.eatcarefully.backend.dto.AchievementDTO;
import com.eatcarefully.backend.helper.JwtHelper;
import com.eatcarefully.backend.model.Product;
import com.eatcarefully.backend.model.achievement.AchievementDefinition;
import com.eatcarefully.backend.model.achievement.AchievementLevel;
import com.eatcarefully.backend.model.achievement.AchievementProgress;
import com.eatcarefully.backend.model.achievement.AchievementType;
import com.eatcarefully.backend.repository.AchievementDefinitionRepository;
import com.eatcarefully.backend.repository.AchievementRepository;
import com.eatcarefully.backend.service.AchievementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AchievementServiceTest {


    @Mock
    private JwtHelper jwtHelper;

    @Mock
    private AchievementRepository progressRepository;

    @Mock
    private AchievementDefinitionRepository definitionRepository;

    @Mock
    private  AchievementDefinition achievementDefinition;


    @InjectMocks
    private AchievementService achievementService;


    private Jwt jwt;
    private Product product;

    @BeforeEach
    void setUp() {
        jwt = mock(Jwt.class);
        product = mock(Product.class);
        achievementDefinition = new AchievementDefinition();
        achievementDefinition.setId(1L);
        achievementDefinition.setAchievementType(AchievementType.NOVA_SCORE);
        achievementDefinition.setAchievementParameter("Healthy");

    }


    @Test
    void Should_Return_UserAchievements() {

        String username = "testUser";
        when(jwtHelper.getUsernameFromToken(any())).thenReturn(username);

        when(progressRepository.findAllByUsername(any())).thenReturn(List.of(
                new AchievementProgress(null, username, achievementDefinition, 0, AchievementLevel.BRONZE)));

        AchievementProgress progress = new AchievementProgress();
        progress.setUsername(username);
        progress.setAchievementDefinition(achievementDefinition);
        progress.setCurrentCount(2);
        progress.setCurrentLevel(AchievementLevel.BRONZE);


        List<AchievementDTO> result = achievementService.getUserAchievements(jwt);

        assertEquals(1, result.size());
        assertEquals(AchievementLevel.BRONZE, result.get(0).level());
        assertEquals(achievementDefinition.getName(), result.get(0).achievementName());
    }


}
