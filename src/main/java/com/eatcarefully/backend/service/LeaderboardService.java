package com.eatcarefully.backend.service;

import com.eatcarefully.backend.dto.LeaderboardDTO;
import com.eatcarefully.backend.dto.LeaderboardRowDTO;
import com.eatcarefully.backend.helper.JwtHelper;
import com.eatcarefully.backend.model.leaderboard.LeaderboardPosition;
import com.eatcarefully.backend.model.leaderboard.PointEvent;
import com.eatcarefully.backend.repository.LeaderboardPositionRepository;
import com.eatcarefully.backend.repository.PointEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardService {

    private final JwtHelper jwtHelper;
    private final PointEventRepository pointEventRepository;
    private final LeaderboardPositionRepository leaderboardPositionRepository;

    public void addPointsForPurchase(String username, String productBarcode, String nutriscore) {
        Optional<PointEvent> existingPointEventOptional = pointEventRepository.findFirstByUsernameAndProductBarcode(username, productBarcode);
        if (existingPointEventOptional.isEmpty()) {
            pointEventRepository.save(new PointEvent(null, username, productBarcode));
            updateUserScore(username, nutriscore);
        }
    }

    public LeaderboardDTO getLeaderboardForCurrentUser(Jwt jwt) {
        String username = jwtHelper.getUsernameFromToken(jwt);
        return getLeaderboardForUser(username);
    }

    public LeaderboardDTO getLeaderboardForSearchedUser(String username) {
        Optional<LeaderboardPosition> searchedUserPosition = leaderboardPositionRepository.findByUsername(username);
        if (searchedUserPosition.isPresent()) {
            return getLeaderboardForUser(username);
        } else {
            return null;
        }
    }

    public LeaderboardDTO getLeaderboardForUser(String username) {
        List<LeaderboardPosition> positionsSorted = leaderboardPositionRepository.findAll(Sort.by(Sort.Direction.DESC, "points"));

        if (positionsSorted.isEmpty()) {
            return new LeaderboardDTO(List.of(), List.of(), 0, 0);
        }

        int userPositionIndex = positionsSorted.indexOf(new LeaderboardPosition(null, username, null));

        List<LeaderboardRowDTO> podiumPositions = positionsSorted.stream()
                .limit(3)
                .map(position -> createLeaderboardRow(position, positionsSorted.indexOf(position)))
                .toList();

        int startIndex = 3;
        int endIndex;

        if (userPositionIndex <= 7) {
            // show positions 4-10 if user is in top positions
            endIndex = Math.min(10, positionsSorted.size());
        } else {
            startIndex = Math.max(3, userPositionIndex - 4);
            endIndex = Math.min(userPositionIndex + 5, positionsSorted.size());
        }

        List<LeaderboardRowDTO> remainingPositions = positionsSorted.stream()
                .skip(startIndex)
                .limit(Math.max(0, endIndex - startIndex))
                .map(position -> createLeaderboardRow(position, positionsSorted.indexOf(position)))
                .toList();

        return new LeaderboardDTO(podiumPositions, remainingPositions, userPositionIndex + 1, positionsSorted.size());
    }

    private LeaderboardRowDTO createLeaderboardRow(LeaderboardPosition position, int index) {
        return new LeaderboardRowDTO(index + 1, position.getUsername(), position.getPoints());
    }

    public LeaderboardDTO getEntireLeaderboard() {
        List<LeaderboardPosition> positionsSorted = leaderboardPositionRepository.findAll(Sort.by(Sort.Direction.DESC, "points"));

        List<LeaderboardRowDTO> podiumPositions = positionsSorted.stream()
                .limit(3)
                .map(position -> createLeaderboardRow(position, positionsSorted.indexOf(position)))
                .toList();

        List<LeaderboardRowDTO> remainingPositions = positionsSorted.stream()
                .skip(3)
                .map(position -> createLeaderboardRow(position, positionsSorted.indexOf(position)))
                .toList();

        return new LeaderboardDTO(podiumPositions, remainingPositions, 0, positionsSorted.size());
    }

    // reset point events and leaderboard positions every monday at 0:00
    @Scheduled(cron = "0 0 0 * * MON")
    public void resetLeaderboardAndPointEvents() {
        log.info("Clearing leaderboard data at: " + LocalDateTime.now());
        pointEventRepository.deleteAll();
        leaderboardPositionRepository.deleteAll();
    }

    private void updateUserScore(String username, String nutriscore) {
        int pointsForPurchase;
        if (nutriscore != null) {
            pointsForPurchase = switch (nutriscore.toLowerCase()) {
                case "a" -> 100;
                case "b" -> 80;
                case "c" -> 50;
                case "d" -> 30;
                default -> 10;  // products with nutriscore E or "unknown" get 10 points
            };

            Optional<LeaderboardPosition> userPositionOptional = leaderboardPositionRepository.findByUsername(username);

            if (userPositionOptional.isPresent()) {
                LeaderboardPosition userPosition = userPositionOptional.get();
                userPosition.setPoints(userPosition.getPoints() + pointsForPurchase);
                leaderboardPositionRepository.save(userPosition);
            } else {
                leaderboardPositionRepository.save(new LeaderboardPosition(null, username, pointsForPurchase));
            }
        }
    }
}
