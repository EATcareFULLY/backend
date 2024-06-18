package com.eatcarefully.backend.controller;

import com.eatcarefully.backend.model.Purchase;
import com.eatcarefully.backend.service.PurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {
    private final PurchaseService shoppingService;

    public PurchaseController(PurchaseService shoppingService) {
        this.shoppingService = shoppingService;
    }

    @GetMapping("/add")
    public ResponseEntity<String> addPurchase(Principal principal, @RequestParam("barcode") Long barcode) {
        shoppingService.addPurchase(principal.getName(), /*1234L*/barcode);
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
