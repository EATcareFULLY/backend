package com.eatcarefully.backend.dto;

import com.eatcarefully.backend.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductRecommendationDTO {

    private String id;
    private String barcode;
    private String score;
    private String brand;
    private String imageURL;

}
