package com.eatcarefully.backend.model;

import com.eatcarefully.backend.dto.PurchaseDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private LocalDate purchaseDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "purchase_id")
    private List<PurchaseItem> purchasedItems = new ArrayList<>();


    public void addPurchaseItem(PurchaseItem purchaseItem){

        if(purchaseItem != null){
            purchaseItem.setPurchase(this);
            this.purchasedItems.add(purchaseItem);
        }

    }

    public PurchaseDTO toDTO(){
        return new PurchaseDTO(
                this.getPurchaseDate(),
                this.getPurchasedItems().stream().map(PurchaseItem::toDTO).toList()
        );
    }


    public Optional<PurchaseItem> getPurchaseItemByBarcode(String barcode){

        return this.purchasedItems.stream()
                .filter( item -> item.getProduct().getId().equals(barcode))
                .findFirst();
    }

    public Boolean containsItemWithBarcode(String barcode){

        if(barcode == null || this.purchasedItems.isEmpty())
            return false;

        return this.purchasedItems.stream()
                .anyMatch(item -> item.getProduct().getId().equals(barcode));

    }




    public Boolean removePurchaseItem(PurchaseItem item){

        if(item == null)
            return false;
        return this.purchasedItems.remove(item);

    }


}
