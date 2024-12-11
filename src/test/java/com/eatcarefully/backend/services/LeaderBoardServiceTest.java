package com.eatcarefully.backend.services;

import com.eatcarefully.backend.dto.LeaderboardDTO;
import com.eatcarefully.backend.helper.JwtHelper;
import com.eatcarefully.backend.model.leaderboard.LeaderboardPosition;
import com.eatcarefully.backend.model.leaderboard.PointEvent;
import com.eatcarefully.backend.repository.LeaderboardPositionRepository;
import com.eatcarefully.backend.repository.PointEventRepository;
import com.eatcarefully.backend.service.LeaderboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class LeaderBoardServiceTest {

    @Mock
    private JwtHelper jwtHelper;

    @Mock
    private PointEventRepository pointEventRepository;

    @Mock
    private LeaderboardPositionRepository leaderboardPositionRepository;

    @InjectMocks
    private LeaderboardService leaderboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void Should_AddPointsForNewPurchase() {

        String username = "testUser";
        String productBarcode = "12345";
        String nutriscore = "A";

        when(pointEventRepository.findFirstByUsernameAndProductBarcode(username, productBarcode)).thenReturn(Optional.empty());

        leaderboardService.addPointsForPurchase(username, productBarcode, nutriscore);

        verify(pointEventRepository, times(1)).save(any(PointEvent.class));
        verify(leaderboardPositionRepository, times(1)).findByUsername(username);
        verify(leaderboardPositionRepository, times(1)).save(any(LeaderboardPosition.class));
    }

    @Test
    void Should_NotAddPointsForExistingPurchase() {
        // Given
        String username = "testUser";
        String productBarcode = "12345";
        String nutriscore = "A";

        when(pointEventRepository.findFirstByUsernameAndProductBarcode(username, productBarcode))
                .thenReturn(Optional.of(new PointEvent()));

        leaderboardService.addPointsForPurchase(username, productBarcode, nutriscore);

        verify(pointEventRepository, never()).save(any(PointEvent.class));
        verify(leaderboardPositionRepository, never()).save(any(LeaderboardPosition.class));
    }


    @Test
    void Should_ReturnLeaderboardForCurrentUser() {

        Jwt jwt = mock(Jwt.class);
        String username = "testUser";
        when(jwtHelper.getUsernameFromToken(jwt)).thenReturn(username);

        LeaderboardDTO leaderboard = leaderboardService.getLeaderboardForCurrentUser(jwt);

        assertNotNull(leaderboard);
        verify(jwtHelper, times(1)).getUsernameFromToken(jwt);
        verify(leaderboardPositionRepository, times(1)).findAll((org.springframework.data.domain.Sort.by(Sort.Direction.DESC, "points")));
    }


    @Test
    void Should_ReturnLeaderboardForSearchedUser() {

        String username = "searchedUser";
        when(leaderboardPositionRepository.findByUsername(username)).thenReturn(Optional.of(new LeaderboardPosition()));

        LeaderboardDTO leaderboard = leaderboardService.getLeaderboardForSearchedUser(username);

        assertNotNull(leaderboard);
        verify(leaderboardPositionRepository, times(1)).findByUsername(username);
        verify(leaderboardPositionRepository, times(1)).findAll(any(Sort.class));
    }


    @Test
    void Should_ReturnNullForNonExistingSearchedUser() {

        String username = "nonExistingUser";
        when(leaderboardPositionRepository.findByUsername(username)).thenReturn(Optional.empty());

        LeaderboardDTO leaderboard = leaderboardService.getLeaderboardForSearchedUser(username);

        assertNull(leaderboard);
        verify(leaderboardPositionRepository, times(1)).findByUsername(username);
    }


    @Test
    void Should_ReturnEntireLeaderboard() {

        LeaderboardPosition position1 = new LeaderboardPosition(null, "user1", 100);
        LeaderboardPosition position2 = new LeaderboardPosition(null, "user2", 80);
        LeaderboardPosition position3 = new LeaderboardPosition(null, "user3", 50);

        when(leaderboardPositionRepository.findAll(any(Sort.class)))
                .thenReturn(List.of(position1, position2, position3));

        LeaderboardDTO leaderboard = leaderboardService.getEntireLeaderboard();

        assertNotNull(leaderboard);
        assertEquals(3, leaderboard.totalPositions());
        verify(leaderboardPositionRepository, times(1)).findAll(any(Sort.class));
    }




}
