package com.eatcarefully.backend.controller;

import com.eatcarefully.backend.dto.ScanResponseDTO;
import com.eatcarefully.backend.helper.ImageHelper;
import com.eatcarefully.backend.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    private final ImageHelper imageHelper;

    @GetMapping("/{barcode}")
    public ResponseEntity<ScanResponseDTO> getProductDetailsByBarcode(@PathVariable String barcode) {

        ScanResponseDTO scanResponse = productService.getProductDetails(barcode);

        return ResponseEntity.ok(
                Objects.requireNonNullElseGet(scanResponse, this::dummyReturnProduct)
        );
    }


    private ScanResponseDTO dummyReturnProduct() {
        return new ScanResponseDTO("0", null, null, null, null, null, null, null);
    }
}
