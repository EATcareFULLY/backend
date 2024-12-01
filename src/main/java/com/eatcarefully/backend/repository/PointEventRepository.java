package com.eatcarefully.backend.repository;

import com.eatcarefully.backend.model.leaderboard.PointEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointEventRepository extends JpaRepository<PointEvent, Long> {

    Optional<PointEvent> findFirstByUsernameAndProductBarcode(String username, String barcode);
}


