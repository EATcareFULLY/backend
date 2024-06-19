package com.eatcarefully.backend.service;

import com.eatcarefully.backend.model.Product;
import com.eatcarefully.backend.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ResponseEntity<?> getProductDetailsByBarcode(Long barcode) {
        Optional<Product> product = productRepository.findById(barcode); //TODO: maybe change to findProductByBarcode and leave ID auto generated
        if(product.isEmpty()) {
            // fetch from OpenFoodFacts API
            boolean wasFetched = true;
            if(wasFetched) {
                // save to database
                return ResponseEntity.ok("product details");
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.ok(product.get());
        }
    }

}
