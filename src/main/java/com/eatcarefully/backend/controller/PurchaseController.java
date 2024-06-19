package com.eatcarefully.backend.controller;

import com.eatcarefully.backend.model.Purchase;
import com.eatcarefully.backend.service.PurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/purchases")   //TODO: add global /api/v1 prefix
public class PurchaseController {
    private final PurchaseService shoppingService;

    public PurchaseController(PurchaseService shoppingService) {
        this.shoppingService = shoppingService;
    }

    @PostMapping()
    public ResponseEntity<String> addPurchase(Principal principal, @RequestBody PurchaseRequest purchaseRequest) {
        shoppingService.addPurchase(principal.getName(), purchaseRequest);
        return ResponseEntity.ok("Purchase added successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Purchase>> getAllPurchases(Principal principal) {
        return ResponseEntity.ok(shoppingService.getWholePurchaseHistory(principal.getName()));
    }

    @GetMapping("/range")
    public List<Purchase> getPurchasesByDateRange(
            Principal principal,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate) {
        return shoppingService.getNarrowedPurchaseHistory(principal.getName(), startDate, endDate);
    }
}
