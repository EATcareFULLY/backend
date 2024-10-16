package com.eatcarefully.backend.controller;

import lombok.Getter;

@Getter
public class PurchaseRequest {
    private String barcode;
    private int quantity;
}
