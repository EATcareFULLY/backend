package com.eatcarefully.backend.controller;

import com.eatcarefully.backend.dto.PurchaseDTO;
import com.eatcarefully.backend.dto.RemovePurchaseItemDTO;
import com.eatcarefully.backend.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/purchases")
@Tag(name = "Shopping History", description = "API for managing purchase history")
public class PurchaseController {
    private final PurchaseService shoppingService;

    public PurchaseController(PurchaseService shoppingService) {
        this.shoppingService = shoppingService;
    }

    @PostMapping()
    public ResponseEntity<String> addPurchase(@AuthenticationPrincipal Jwt jwt, @RequestBody PurchaseRequest purchaseRequest) {
        if (purchaseRequest.getQuantity() <= 0)
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);


        shoppingService.addPurchaseItem(jwt, purchaseRequest);
        return ResponseEntity.ok("Purchase added successfully");
    }



    @GetMapping("/all")
    @Cacheable(value = "purchases_resp")
    public ResponseEntity<List<PurchaseDTO>> getAllPurchases(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(shoppingService.getWholePurchaseHistory(jwt));
    }

    @GetMapping("/range")
    @Cacheable(value = "purchases_resp") //TODO: caching based on pageable
    @Operation(summary = "Get purchases by date range")
    public Page<PurchaseDTO> getPurchasesByDateRange(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(name = "startDate") LocalDate startDate,
            @RequestParam(name = "endDate") LocalDate endDate,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "desc") String sort) {

        Sort.Direction direction = Sort.Direction.ASC;
        String property = "purchaseDate";

        if (sort.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = PageRequest.of(page, size, direction, property);

        return shoppingService.getNarrowedPurchaseHistory(jwt, startDate, endDate, pageable);
    }

    @DeleteMapping()
    public ResponseEntity<?> removePurchaseItem(@AuthenticationPrincipal Jwt jwt,
                                                     @RequestBody RemovePurchaseItemDTO removeRequest) {

        if(removeRequest.getQuantity() <= 0){
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        else{

            if(shoppingService.removePurchaseItem(jwt, removeRequest.getBarcode(),removeRequest.getPurchaseDate(), removeRequest.getQuantity()))
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            else
                return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);

        }

    }






}
