package com.eatcarefully.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class PurchaseDTO {
    private LocalDate purchaseDate;
    private List<PurchaseItemDTO> purchaseItems;
}
