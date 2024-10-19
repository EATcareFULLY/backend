package com.eatcarefully.backend.service;

import com.eatcarefully.backend.controller.PurchaseRequest;
import com.eatcarefully.backend.model.*;
import com.eatcarefully.backend.repository.IngredientRepository;
import com.eatcarefully.backend.repository.ProductRepository;
import com.eatcarefully.backend.repository.PurchaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PurchaseService {

    private final IngredientRepository ingredientRepository;
    private final ProductRepository productRepository;
    private final PurchaseRepository shoppingHistoryRepository;
    private final ProductService productService;

    public void addPurchase(String username, PurchaseRequest purchaseRequest) {

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

        if(! purchase.isPresent()){
            Purchase newPurchase = new Purchase();
            newPurchase.setUsername(username);
            newPurchase.setPurchaseDate(LocalDate.now());
            shoppingHistoryRepository.save(newPurchase);
            return newPurchase;

        }
        else{
            return purchase.get();
        }

    }




    public List<Purchase> getWholePurchaseHistory(String username) {
        return shoppingHistoryRepository.findByUsername(username);
    }

    public List<Purchase> getNarrowedPurchaseHistory(String username, LocalDate start, LocalDate end) {
        return shoppingHistoryRepository.findByUsernameAndPurchaseDateBetween(username, start, end);
    }
}
