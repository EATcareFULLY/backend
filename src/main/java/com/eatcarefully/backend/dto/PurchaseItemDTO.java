package com.eatcarefully.backend.dto;

import com.eatcarefully.backend.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PurchaseItemDTO {
    private Product product;
    private int quantity;
}
