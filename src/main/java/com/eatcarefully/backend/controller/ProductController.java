package com.eatcarefully.backend.controller;

import com.eatcarefully.backend.dto.ProductRecommendationDTO;
import com.eatcarefully.backend.model.Product;
import com.eatcarefully.backend.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{barcode}")
    @Cacheable( cacheNames = "products_resp", key = "#barcode")
    public ResponseEntity<?> getProductDetailsByBarcode(@PathVariable String barcode) {

        Product product = productService.getProductByBarcodeFromDatabaseOrOpenFoodFacts(barcode);

        if(product == null) {
            Product temp = new Product();
            temp.setId("0");
            return ResponseEntity.ok(temp);
        }
        else
            return ResponseEntity.ok(product);

    }

    // mock endpoint for recommendation system

    @GetMapping("/{barcode}/recommend")
    public ResponseEntity<?> getProductRecommendationByBarcode(@PathVariable String barcode){

        Product product = productService.getProductByBarcodeFromDatabase(barcode);

        //mock products to recommend

        if( product != null){

            String testingPossumUrl = "https://i.pinimg.com/474x/84/6f/85/846f8591330851c3ba9e11c3ea1afaa8.jpg";
            List<ProductRecommendationDTO> recommendations = List.of(
                    new ProductRecommendationDTO("1", "Product01", "A", "Brand1", testingPossumUrl),
                    new ProductRecommendationDTO("2", "Product02", "B", "Brand3", testingPossumUrl),
                    new ProductRecommendationDTO("3", "Product03", "C", "Brand2", testingPossumUrl)
            );

            return ResponseEntity.ok(recommendations);

            }

        return ResponseEntity.notFound().build();

        }

    @PostMapping("/scan-label")
    public ResponseEntity<?> getLabelEvaluation(@RequestBody Map<String, String> request){

        String labelText = request.get("labelText");

        if(labelText.isEmpty() || labelText.isBlank())
            return ResponseEntity.badRequest().build();

        String eval = labelText + ": Mock eval response";

        return ResponseEntity.ok(eval);

    }



}
