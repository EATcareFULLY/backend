package com.eatcarefully.backend.controller;

import com.eatcarefully.backend.dto.ProductRecommendationDTO;
import com.eatcarefully.backend.dto.ScanResponseDTO;
import com.eatcarefully.backend.helper.ImageHelper;
import com.eatcarefully.backend.model.Product;
import com.eatcarefully.backend.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    private final ImageHelper imageHelper;

    @GetMapping("/{barcode}")
    @Cacheable(cacheNames = "products_resp", key = "#barcode")
    public ResponseEntity<ScanResponseDTO> getProductDetailsByBarcode(@AuthenticationPrincipal Jwt jwt, @PathVariable String barcode) {

        ScanResponseDTO scanResponse = productService.getProductByBarcodeFromDatabaseOrOpenFoodFacts(jwt, barcode);

        return ResponseEntity.ok(
                Objects.requireNonNullElseGet(scanResponse, this::dummyReturnProduct)
        );
    }

    // mock endpoint for recommendation system

    @GetMapping("/{barcode}/recommend")
    public ResponseEntity<?> getProductRecommendationByBarcode(@PathVariable String barcode) {

        Product product = productService.getProductByBarcodeFromDatabase(barcode);

        //mock products to recommend

        if (product != null) {

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

    @PostMapping("/eval-label")
    public ResponseEntity<?> getLabelEvaluation(@RequestParam("file") MultipartFile file) throws IOException {

        try {

            if (file == null || file.isEmpty())
                return ResponseEntity.badRequest().build();

            // check file extension

            if (imageHelper.isFileExtensionSupported(file)) {

                // load file
                BufferedImage image = imageHelper.convertMultipartFileToBufferedImage(file);

                int width = image.getWidth();
                int height = image.getHeight();

                String eval = String.format("File size: %d x %d px", width, height);

                return ResponseEntity.ok(eval);
            } else
                return ResponseEntity.badRequest().build();


        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
    }

    private ScanResponseDTO dummyReturnProduct() {
        return new ScanResponseDTO("0", null, null, null, null, null, null, null, null);
    }
}
