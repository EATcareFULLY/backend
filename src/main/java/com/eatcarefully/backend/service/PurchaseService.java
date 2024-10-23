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

    public void addPurchaseItem(Jwt jwt, PurchaseRequest purchaseRequest) {
        String username = getUsernameFromToken(jwt);

        // get or create purchase
        Purchase purchase = getOrCreatePurchase(username, LocalDate.now());

        // get product
        Product product = productService.getProductByBarcode(purchaseRequest.getBarcode());

        // alter and add purchase item
        if( product != null){

            Optional<PurchaseItem> itemOpt = purchase.getPurchaseItemByBarcode(purchaseRequest.getBarcode());

            if(itemOpt.isPresent()){

                PurchaseItem item = itemOpt.get();
                item.setQuantity(item.getQuantity() + purchaseRequest.getQuantity());

            }
            else{
                PurchaseItem newItem = new PurchaseItem();
                newItem.setProduct(product);
                newItem.setQuantity(purchaseRequest.getQuantity());
                purchase.addPurchaseItem(newItem);

            }
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



    public Boolean removePurchaseItem(Jwt jwt, String barcode, LocalDate purchaseDate, int quantity){

        String username = getUsernameFromToken(jwt);

        // check if purchase exists
        Optional<Purchase> purchaseOpt = shoppingHistoryRepository.findByUsernameAndPurchaseDate(username, purchaseDate);

        if( purchaseOpt.isEmpty())
            // purchase not found
            return false;

        // check if purchase item exists
        Optional<PurchaseItem> itemOpt = purchaseOpt.get().getPurchaseItemByBarcode(barcode);

        if(itemOpt.isEmpty())
            //purchaseItem not found
            return false;

        PurchaseItem item = itemOpt.get();
        Purchase purchase = purchaseOpt.get();

        if(quantity > item.getQuantity())
            return false;

        if(quantity == item.getQuantity()){
            // remove object
            purchase.removePurchaseItem(item);
        }
        else{
            item.setQuantity(item.getQuantity() - quantity);
        }

        shoppingHistoryRepository.save(purchase);
        return true;

    }


}



