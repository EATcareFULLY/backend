package com.eatcarefully.backend.service;

import com.eatcarefully.backend.model.Ingredient;
import com.eatcarefully.backend.model.Product;
import com.eatcarefully.backend.model.Purchase;
import com.eatcarefully.backend.repository.IngredientRepository;
import com.eatcarefully.backend.repository.ProductRepository;
import com.eatcarefully.backend.repository.PurchaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PurchaseService {

    private final IngredientRepository ingredientRepository;
    private final ProductRepository productRepository;
    private final PurchaseRepository shoppingHistoryRepository;

    public void addPurchase(String username, Long productId) {
        Ingredient ingredient1 = ingredientRepository.save(new Ingredient(18066L, "water", "PLACEHOLDER - no desc yet", 21.75f));
        Ingredient ingredient2 = ingredientRepository.save(new Ingredient(9100L, "long-grain-rice", "PLACEHOLDER - no desc yet", 7.25f));
        Product product = productRepository.save(
                new Product(
                        productId,
                        "High Protein Chicken & Chorizo Paella",
                        "B",
                        "Muscle Foood",
                        "https://images.openfoodfacts.org/images/products/505/590/422/3289/front_en.3.400.jpg",
                        List.of(),
                        List.of(ingredient1, ingredient2)
                ));
        shoppingHistoryRepository.save(new Purchase(null, username, product/*productRepository.findById(productId).orElseThrow()*/, LocalDateTime.now()));
    }

    public List<Purchase> getWholePurchaseHistory(String username) {
        return shoppingHistoryRepository.findByUsername(username);
    }

    public List<Purchase> getNarrowedPurchaseHistory(String username, LocalDateTime start, LocalDateTime end) {
        return shoppingHistoryRepository.findByUsernameAndPurchaseDateBetween(username, start, end);
    }
}
