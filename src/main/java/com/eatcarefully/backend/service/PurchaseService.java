package com.eatcarefully.backend.service;

import com.eatcarefully.backend.controller.PurchaseRequest;
import com.eatcarefully.backend.dto.PurchaseDTO;
import com.eatcarefully.backend.model.*;
import com.eatcarefully.backend.repository.IngredientRepository;
import com.eatcarefully.backend.repository.ProductRepository;
import com.eatcarefully.backend.repository.PurchaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PurchaseService {

    private static final String USERNAME_CLAIM = "preferred_username";
    private final IngredientRepository ingredientRepository;
    private final ProductRepository productRepository;
    private final PurchaseRepository shoppingHistoryRepository;
    private final ProductService productService;

    public void addPurchase(Jwt jwt, PurchaseRequest purchaseRequest) {
        String username = getUsernameFromToken(jwt);

        // get or create purchase
        Purchase purchase = getOrCreatePurchase(username, LocalDate.now());

        // get product
        Product product = productService.getProductByBarcode(purchaseRequest.getBarcode());

        // alter and add purchase item
        if( product != null){
            PurchaseItem purchaseItem = new PurchaseItem();
            purchaseItem.setProduct(product);
            purchaseItem.setQuantity(purchaseRequest.getQuantity());
            purchase.addPurchaseItem(purchaseItem);
            shoppingHistoryRepository.save(purchase);
        }
    }

    public Purchase getOrCreatePurchase(String username, LocalDate purchaseDate){

        Optional<Purchase> purchase = shoppingHistoryRepository.findByUsernameAndPurchaseDate(username, purchaseDate);

        if(purchase.isEmpty()){
            Purchase newPurchase = new Purchase();
            newPurchase.setUsername(username);
            newPurchase.setPurchaseDate(LocalDate.now());
            shoppingHistoryRepository.save(newPurchase);
            return newPurchase;
        } else {
            return purchase.get();
        }
    }

    public List<PurchaseDTO> getWholePurchaseHistory(Jwt jwt) {
        String username = getUsernameFromToken(jwt);
        List<Purchase> purchases = shoppingHistoryRepository.findByUsername(username);
        return purchases.stream()
                .map(Purchase::toDTO)
                .toList();
    }

    public Page<PurchaseDTO> getNarrowedPurchaseHistory(Jwt jwt, LocalDate start, LocalDate end, Pageable pageable) {
        String username = getUsernameFromToken(jwt);
        Page<Purchase> purchasesPage = shoppingHistoryRepository.findByUsernameAndPurchaseDateBetween(username, start, end, pageable);
        return purchasesPage.map(Purchase::toDTO);
    }

    private String getUsernameFromToken(Jwt jwt){
        return jwt.getClaim(USERNAME_CLAIM);
    }
}
