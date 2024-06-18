package com.eatcarefully.backend.repository;

import com.eatcarefully.backend.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByUsername(String username);
    List<Purchase> findByUsernameAndPurchaseDateBetween(String username, LocalDateTime start, LocalDateTime end);

}
