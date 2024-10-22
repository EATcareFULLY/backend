package com.eatcarefully.backend.dto;


import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RemovePurchaseItemDTO {

    private String barcode;
    private LocalDate purchaseDate;
    private int quantity;
}
