package com.eatcarefully.backend.service;

import com.eatcarefully.backend.controller.PurchaseRequest;
import com.eatcarefully.backend.dto.HistoryAnalysisProductDTO;
import com.eatcarefully.backend.dto.PurchaseDTO;
import com.eatcarefully.backend.helper.JwtHelper;
import com.eatcarefully.backend.model.*;
import com.eatcarefully.backend.repository.PurchaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductService productService;
    private final JwtHelper jwtHelper;

    public void addPurchaseItem(Jwt jwt, PurchaseRequest purchaseRequest) {
        String username = jwtHelper.getUsernameFromToken(jwt);

        // get or create purchase
        Purchase purchase = getOrCreatePurchase(username, LocalDate.now());

        // get product
        Product product = productService.getProductByBarcodeFromDatabase(purchaseRequest.getBarcode());

        // alter and add purchase item
        if( product != null){

            Optional<PurchaseItem> itemOpt = purchase.getPurchaseItemByBarcode(purchaseRequest.getBarcode());

            if(itemOpt.isPresent()){

                PurchaseItem item = itemOpt.get();
                item.setQuantity(item.getQuantity() + purchaseRequest.getQuantity());

            } else {
                PurchaseItem newItem = new PurchaseItem();
                newItem.setProduct(product);
                newItem.setQuantity(purchaseRequest.getQuantity());
                purchase.addPurchaseItem(newItem);
            }
            purchaseRepository.save(purchase);

        }
    }

    public Purchase getOrCreatePurchase(String username, LocalDate purchaseDate){

        Optional<Purchase> purchase = purchaseRepository.findByUsernameAndPurchaseDate(username, purchaseDate);

        if(purchase.isEmpty()){
            Purchase newPurchase = new Purchase();
            newPurchase.setUsername(username);
            newPurchase.setPurchaseDate(LocalDate.now());
            purchaseRepository.save(newPurchase);
            return newPurchase;
        } else {
            return purchase.get();
        }
    }

    public List<PurchaseDTO> getWholePurchaseHistory(Jwt jwt) {
        String username = jwtHelper.getUsernameFromToken(jwt);
        List<Purchase> purchases = purchaseRepository.findByUsername(username);
        return purchases.stream()
                .map(Purchase::toDTO)
                .toList();
    }

    public Page<PurchaseDTO> getNarrowedPurchaseHistory(Jwt jwt, LocalDate start, LocalDate end, Pageable pageable) {
        String username = jwtHelper.getUsernameFromToken(jwt);
        Page<Purchase> purchasesPage = purchaseRepository.findByUsernameAndPurchaseDateBetween(username, start, end, pageable);
        return purchasesPage.map(Purchase::toDTO);
    }



    public Boolean removePurchaseItem(Jwt jwt, String barcode, LocalDate purchaseDate, int quantity){

        String username = jwtHelper.getUsernameFromToken(jwt);

        // check if purchase exists
        Optional<Purchase> purchaseOpt = purchaseRepository.findByUsernameAndPurchaseDate(username, purchaseDate);

        if( purchaseOpt.isEmpty())
            // purchase not found
            return false;

        // check if purchase item exists
        Optional<PurchaseItem> itemOpt = purchaseOpt.get().getPurchaseItemByBarcode(barcode);

        if(itemOpt.isEmpty())
            //purchaseItem not found
            return false;

        PurchaseItem item = itemOpt.get();
        Purchase purchase = purchaseOpt.get();

        if(quantity > item.getQuantity())
            return false;

        if(quantity == item.getQuantity()){
            // remove object
            purchase.removePurchaseItem(item);
        }
        else{
            item.setQuantity(item.getQuantity() - quantity);
        }

        purchaseRepository.save(purchase);
        return true;

    }

    public List<HistoryAnalysisProductDTO> getDataForHistoryAnalysis(Jwt jwt){

        String username = jwtHelper.getUsernameFromToken(jwt);
        List<Purchase> purchases = purchaseRepository.findByUsername(username);

        List<HistoryAnalysisProductDTO> mappedList = purchases.stream()
                .map(Purchase::toListOfHistoryAnalysisProductDTO)
                .flatMap(List::stream)
                .toList();

        return mappedList;

    }


    public List<String> getBarcodesFromLeastHealthyProductsPurchasedOn(String username, LocalDate date, int barcodesNum){

        Purchase purchase = purchaseRepository.findByUsernameAndPurchaseDate(username, date).orElse(null);

        if(purchase == null)
            return null;

        ArrayList<Product> purchasesProducts = new ArrayList<>(purchase.getPurchasedItems().stream().map(PurchaseItem::getProduct).toList());

        purchasesProducts.sort( (p1,p2) -> p2.getScore().compareTo(p1.getScore()));

        return purchasesProducts.stream().limit(barcodesNum).map(Product::getId).toList();


    }





}



