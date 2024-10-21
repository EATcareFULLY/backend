package com.eatcarefully.backend.model;

import com.eatcarefully.backend.dto.PurchaseItemDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PurchaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;


    public PurchaseItemDTO toDTO(){
        return new PurchaseItemDTO(
                this.getProduct(),
                this.getQuantity()
        );
    }

}
