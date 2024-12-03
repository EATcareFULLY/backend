package com.eatcarefully.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PurchaseRequestDTO {
    private String barcode;
    private int quantity;
}
