package com.eatcarefully.backend.controller;

import com.eatcarefully.backend.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{barcode}")
    @Cacheable( cacheNames = "products_resp", key = "#barcode")
    public ResponseEntity<?> getProductDetailsByBarcode(@PathVariable String barcode) {


        return productService.getProductDetailsByBarcode(barcode);
    }
}
