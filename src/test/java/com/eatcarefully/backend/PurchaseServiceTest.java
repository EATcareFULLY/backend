package com.eatcarefully.backend;

import com.eatcarefully.backend.dto.PurchaseRequestDTO;
import com.eatcarefully.backend.dto.PurchaseDTO;
import com.eatcarefully.backend.helper.JwtHelper;
import com.eatcarefully.backend.model.Product;
import com.eatcarefully.backend.model.Purchase;
import com.eatcarefully.backend.model.PurchaseItem;
import com.eatcarefully.backend.repository.PurchaseRepository;
import com.eatcarefully.backend.service.AchievementService;
import com.eatcarefully.backend.service.LeaderboardService;
import com.eatcarefully.backend.service.ProductService;
import com.eatcarefully.backend.service.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTest {

    private static final String VALID_BARCODE = "12345678";
    private static final String USERNAME = "testuser";
    private final PurchaseRequestDTO PURCHASE_REQUEST = new PurchaseRequestDTO("12345678", 1);

    @InjectMocks
    private PurchaseService purchaseService;

    @Mock
    private ProductService productService;

    @Mock
    private JwtHelper jwtHelper;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private AchievementService achievementService;

    @Mock LeaderboardService leaderboardService;

    @Mock
    private Jwt jwt;



    @BeforeEach
    public void setUp() {
        when(jwtHelper.getUsernameFromToken(any())).thenReturn(USERNAME);
    }

    @Test
    public void Should_AddPurchaseItemToExistingPurchase_When_ProductIsNotFoundInPurchase(){

        // Given
        Product product = new Product();
        when(purchaseRepository.save(any())).thenReturn(null);
        when(productService.getProductByBarcodeFromDatabase(any())).thenReturn(product);
        Optional<Purchase> purchase = Optional.of(
                new Purchase(1234L, USERNAME, LocalDate.now(), new ArrayList<>())
        );
        when(purchaseRepository.findByUsernameAndPurchaseDate(any(), any())).thenReturn(purchase);

        // When
        purchaseService.addPurchaseItem(jwt, PURCHASE_REQUEST);

        // Then
        verify(purchaseRepository, times(1)).save(any());
    }

    @Test
    public void Should_CreatePurchase_When_PurchaseIsNotFound(){

        // Given
        Product product = new Product();
        when(purchaseRepository.save(any())).thenReturn(null);
        when(productService.getProductByBarcodeFromDatabase(any())).thenReturn(product);
        when(purchaseRepository.findByUsernameAndPurchaseDate(any(), any())).thenReturn(Optional.empty());

        // When
        purchaseService.addPurchaseItem(jwt, PURCHASE_REQUEST);

        // Then
        verify(purchaseRepository, times(2)).save(any());
    }

    @Test
    public void Should_ReturnListOfOnePurchase_When_GettingPurchaseHistory(){

        // Given
        List<Purchase> purchaseList = List.of(new Purchase(1234L, USERNAME, LocalDate.now(), new ArrayList<>()));
        when(purchaseRepository.findByUsername(any())).thenReturn(purchaseList);

        // When
        List<PurchaseDTO> result = purchaseService.getWholePurchaseHistory(jwt);

        // Then
        assertEquals(1, result.size());
    }

    @Test
    public void Should_ReturnPage_When_GettingNarrowedPurchaseHistory(){

        // Given
        List<Purchase> purchases = List.of(new Purchase(1234L, USERNAME, LocalDate.now(), new ArrayList<>()));
        Page<Purchase> purchasePage = new PageImpl<>(purchases, PageRequest.of(0, 10), purchases.size());
        when(purchaseRepository.findByUsernameAndPurchaseDateBetween(any(), any(), any(), any())).thenReturn(purchasePage);

        // When
        Page<PurchaseDTO> result = purchaseService.getNarrowedPurchaseHistory(jwt, LocalDate.now(), LocalDate.now(), PageRequest.of(0, 10));

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }


    @Test
    public void Should_ReturnTrue_When_RemovingExistingPurchaseItem() {
        // Given
        Product product = new Product();
        product.setId(VALID_BARCODE);
        Purchase purchase = new Purchase(1234L, USERNAME, LocalDate.now(), new ArrayList<>());
        PurchaseItem purchaseItem = new PurchaseItem();
        purchaseItem.setProduct(product);
        purchaseItem.setQuantity(2);
        purchase.addPurchaseItem(purchaseItem);

        when(purchaseRepository.findByUsernameAndPurchaseDate(any(), any()))
                .thenReturn(Optional.of(purchase));
        when(purchaseRepository.save(any())).thenReturn(null);

        // When
        Boolean result = purchaseService.removePurchaseItem(jwt, VALID_BARCODE, LocalDate.now(), 1);

        // Then
        assertTrue(result);
        verify(purchaseRepository, times(1)).save(any());
    }

    @Test
    public void Should_ReturnFalse_When_RemovingNonExistentPurchase() {
        // Given
        when(purchaseRepository.findByUsernameAndPurchaseDate(any(), any()))
                .thenReturn(Optional.empty());

        // When
        Boolean result = purchaseService.removePurchaseItem(jwt, VALID_BARCODE, LocalDate.now(), 1);

        // Then
        assertFalse(result);
        verify(purchaseRepository, never()).save(any());
    }











}
