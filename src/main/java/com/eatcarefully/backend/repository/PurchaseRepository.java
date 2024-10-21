package com.eatcarefully.backend.repository;

import com.eatcarefully.backend.model.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByUsername(String username);
    Page<Purchase> findByUsernameAndPurchaseDateBetween(String username, LocalDate start, LocalDate end, Pageable pageable);
    Optional<Purchase> findByUsernameAndPurchaseDate(String username, LocalDate purchaseDate);

}
