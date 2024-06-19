package com.eatcarefully.backend.controller;

import lombok.Getter;

@Getter
public class PurchaseRequest {
    private Long barcode;
    private int quantity;
}
