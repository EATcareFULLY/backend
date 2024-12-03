package com.eatcarefully.backend.model.leaderboard;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "leaderboard_position", indexes = {
        @Index(name = "idx_points", columnList = "points")
})
public class LeaderboardPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private Integer points;
//    private Integer position;
//    private Integer weekNumber;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeaderboardPosition that = (LeaderboardPosition) o;
        return Objects.equals(username, that.username);
    }
}
