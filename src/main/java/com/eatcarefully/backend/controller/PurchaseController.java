package com.eatcarefully.backend.controller;

import com.eatcarefully.backend.model.Purchase;
import com.eatcarefully.backend.service.PurchaseService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {
    private final PurchaseService shoppingService;

    public PurchaseController(PurchaseService shoppingService) {
        this.shoppingService = shoppingService;
    }

    @PostMapping()
    public ResponseEntity<String> addPurchase(@AuthenticationPrincipal Jwt jwt, @RequestBody PurchaseRequest purchaseRequest) {
        shoppingService.addPurchase(jwt, purchaseRequest);
        return ResponseEntity.ok("Purchase added successfully");
    }



    @GetMapping("/all")
    @Cacheable(value = "purchases_resp")
    public ResponseEntity<List<Purchase>> getAllPurchases(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(shoppingService.getWholePurchaseHistory(jwt));
    }

    @GetMapping("/range")
    @Cacheable(value = "purchases_resp")
    public List<Purchase> getPurchasesByDateRange(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {
        return shoppingService.getNarrowedPurchaseHistory(jwt, startDate, endDate);
    }
}
