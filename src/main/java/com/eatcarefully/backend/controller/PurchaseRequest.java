package com.eatcarefully.backend.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PurchaseRequest {
    private String barcode;
    private int quantity;
}
