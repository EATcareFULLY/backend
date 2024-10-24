package com.eatcarefully.backend;

import com.eatcarefully.backend.controller.ProductController;
import com.eatcarefully.backend.controller.PurchaseController;
import com.eatcarefully.backend.dto.PurchaseDTO;
import com.eatcarefully.backend.model.Product;
import com.eatcarefully.backend.model.Purchase;
import com.eatcarefully.backend.model.PurchaseItem;
import com.eatcarefully.backend.service.ProductService;
import com.eatcarefully.backend.service.PurchaseService;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PurchaseController.class)
public class PurchaseControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PurchaseService purchaseService;


    private static final String USERNAME_CLAIM = "preferred_username";
    private static final String VALID_BARCODE = "11111111";





    private static Jwt createJwtToken(String username) {

        return new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("test", "test"),
                Map.of(
                        USERNAME_CLAIM, username  // Custom claim

                )
        );
    }


    private static List<Purchase> getPurchases(){

        Product product = new Product();
        product.setId(VALID_BARCODE);


        return      List.of(
                new Purchase(0L, "user", LocalDate.of(2024, 1, 15), List.of(new PurchaseItem(null, null, product, 10))),
                new Purchase(1L, "user", LocalDate.of(2024, 1, 20), List.of(new PurchaseItem(null, null, product, 10))),
                new Purchase(2L, "user1", LocalDate.of(2024, 2, 1), List.of(new PurchaseItem(null, null, product, 10))),
                new Purchase(3L, "user", LocalDate.of(2024, 2, 2), List.of(new PurchaseItem(null, null, product, 10)))

        );

    }



    @Test
    public void Should_ReturnUnprocessableEntity_When_AddQuantityIs0() throws Exception {

        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("barcode", VALID_BARCODE);
        jsonPayload.put("quantity", 0);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/purchases")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .content(String.valueOf(jsonPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

    }

    @Test
    public void Should_ReturnUnprocessableEntity_When_AddQuantityIsNegative() throws Exception {

        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("barcode", VALID_BARCODE);
        jsonPayload.put("quantity", -1);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/purchases")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .content(String.valueOf(jsonPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

    }

    @Test
    public void Should_ReturnOk_When_QuantityIsPositive() throws Exception {

        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("barcode", VALID_BARCODE);
        jsonPayload.put("quantity", 3);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/purchases")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .content(String.valueOf(jsonPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }



    @Test
    public void Should_ReturnUserPurchases_When_UserAndPurchaseExist() throws Exception {


        when(purchaseService.getWholePurchaseHistory(Mockito.any(Jwt.class))).thenAnswer(
                new Answer<List<PurchaseDTO>>(){

                    @Override
                    public List<PurchaseDTO> answer(InvocationOnMock invocation) throws Throwable{

                        Jwt jwt = invocation.getArgument(0, Jwt.class);

                        return getPurchases().stream()
                                .filter(purchase -> purchase.getUsername().equals(jwt.getClaim(USERNAME_CLAIM)))
                                .map(Purchase::toDTO)
                                .toList();

                    }});


        Jwt jwt = createJwtToken("user");

        this.mockMvc.perform(MockMvcRequestBuilders.get("/purchases/all")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3)


        );

    }

    @Test
    public void Should_ReturnEmptyList_When_UserHasNoPurchases() throws Exception {


        when(purchaseService.getWholePurchaseHistory(Mockito.any(Jwt.class))).thenAnswer(
                new Answer<List<PurchaseDTO>>(){

                    @Override
                    public List<PurchaseDTO> answer(InvocationOnMock invocation) throws Throwable{

                        Jwt jwt = invocation.getArgument(0, Jwt.class);

                        return getPurchases().stream()
                                .filter(purchase -> purchase.getUsername().equals(jwt.getClaim(USERNAME_CLAIM)))
                                .map(Purchase::toDTO)
                                .toList();

                    }});


        Jwt jwt = createJwtToken("noPurchaseUser");

        this.mockMvc.perform(MockMvcRequestBuilders.get("/purchases/all")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0)


                );

    }


    @Test
    public void Should_ReturnUserPurchasesFromRange_When_UserAndPurchasesExists() throws Exception {


        when(purchaseService.getNarrowedPurchaseHistory(Mockito.any(Jwt.class)
                ,Mockito.any(LocalDate.class)
                ,Mockito.any(LocalDate.class),
                Mockito.any(Pageable.class)
        )).thenAnswer(
                (Answer<Page<PurchaseDTO>>) invocation -> {

                    Jwt jwt = invocation.getArgument(0, Jwt.class);
                    LocalDate start = invocation.getArgument(1, LocalDate.class);
                    LocalDate end = invocation.getArgument(2, LocalDate.class);
                    Pageable pageable = invocation.getArgument(3, Pageable.class);

                    List<PurchaseDTO> list =  getPurchases().stream()
                            .filter(purchase -> purchase.getUsername().equals(jwt.getClaim(USERNAME_CLAIM)))
                            .filter(purchase -> purchase.getPurchaseDate().isAfter(start) && purchase.getPurchaseDate().isBefore(end))
                            .map(Purchase::toDTO)
                            .toList();

                    return new PageImpl<>(list, pageable, list.size());

                });


        Jwt jwt = createJwtToken("user");

        this.mockMvc.perform(MockMvcRequestBuilders.get("/purchases/range")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-29")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "desc")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2)

                );

    }

    @Test
    public void Should_ReturnEmptyPage_When_UserPurchasesDoNotExists() throws Exception {


        when(purchaseService.getNarrowedPurchaseHistory(Mockito.any(Jwt.class)
                ,Mockito.any(LocalDate.class)
                ,Mockito.any(LocalDate.class),
                Mockito.any(Pageable.class)
        )).thenAnswer(
                (Answer<Page<PurchaseDTO>>) invocation -> {

                    Jwt jwt = invocation.getArgument(0, Jwt.class);
                    LocalDate start = invocation.getArgument(1, LocalDate.class);
                    LocalDate end = invocation.getArgument(2, LocalDate.class);
                    Pageable pageable = invocation.getArgument(3, Pageable.class);

                    List<PurchaseDTO> list =  getPurchases().stream()
                            .filter(purchase -> purchase.getUsername().equals(jwt.getClaim(USERNAME_CLAIM)))
                            .filter(purchase -> purchase.getPurchaseDate().isAfter(start) && purchase.getPurchaseDate().isBefore(end))
                            .map(Purchase::toDTO)
                            .toList();

                    return new PageImpl<>(list, pageable, list.size());

                });


        Jwt jwt = createJwtToken("user1");

        this.mockMvc.perform(MockMvcRequestBuilders.get("/purchases/range")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-29")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "desc")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0)

                );

    }


    @Test
    public void Should_ReturnUnprocessableEntity_When_DeleteQuantityIs0() throws Exception {

        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("barcode", VALID_BARCODE);
        jsonPayload.put("purchaseDate", "2024-01-15");
        jsonPayload.put("quantity", 0);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/purchases")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .content(String.valueOf(jsonPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

    }

    @Test
    public void Should_ReturnUnprocessableEntity_When_DeleteQuantityIsNegative() throws Exception {

        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("barcode", VALID_BARCODE);
        jsonPayload.put("purchaseDate", "2024-01-15");
        jsonPayload.put("quantity", -1);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/purchases")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .content(String.valueOf(jsonPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

    }


    @Test
    public void Should_ReturnOk_When_ItemExistsAndQuantityIsOk() throws Exception {

        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("barcode", VALID_BARCODE);
        jsonPayload.put("purchaseDate", "2024-01-15");
        jsonPayload.put("quantity", 5);

        when(purchaseService.removePurchaseItem(Mockito.any(Jwt.class)
                ,Mockito.any(String.class)
                ,Mockito.any(LocalDate.class),
                Mockito.any(Integer.class)
        )).thenAnswer(
                (Answer<Boolean>) invocation -> {

                    Jwt jwt = invocation.getArgument(0, Jwt.class);
                    String barcode = invocation.getArgument(1, String.class);
                    LocalDate date = invocation.getArgument(2, LocalDate.class);
                    Integer quantity = invocation.getArgument(3, Integer.class);

                    return  getPurchases().stream()
                            .filter(purchase -> purchase.getUsername().equals(jwt.getClaim(USERNAME_CLAIM)))
                            .filter(purchase -> purchase.getPurchaseDate().isEqual(date) && purchase.containsItemWithBarcode(barcode))
                            .anyMatch(purchase -> purchase.getPurchaseItemByBarcode(barcode).stream().anyMatch(item -> item.getQuantity() >= quantity));






                });

        Jwt jwt = createJwtToken("user");

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/purchases")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                        .content(String.valueOf(jsonPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

    }

    @Test
    public void Should_ReturnOk_When_ItemExistsAndQuantityIsTooBig() throws Exception {

        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("barcode", VALID_BARCODE);
        jsonPayload.put("purchaseDate", "2024-01-15");
        jsonPayload.put("quantity", 50);

        when(purchaseService.removePurchaseItem(Mockito.any(Jwt.class)
                ,Mockito.any(String.class)
                ,Mockito.any(LocalDate.class),
                Mockito.any(Integer.class)
        )).thenAnswer(
                (Answer<Boolean>) invocation -> {

                    Jwt jwt = invocation.getArgument(0, Jwt.class);
                    String barcode = invocation.getArgument(1, String.class);
                    LocalDate date = invocation.getArgument(2, LocalDate.class);
                    Integer quantity = invocation.getArgument(3, Integer.class);

                    return  getPurchases().stream()
                            .filter(purchase -> purchase.getUsername().equals(jwt.getClaim(USERNAME_CLAIM)))
                            .filter(purchase -> purchase.getPurchaseDate().isEqual(date) && purchase.containsItemWithBarcode(barcode))
                            .anyMatch(purchase -> purchase.getPurchaseItemByBarcode(barcode).stream().anyMatch(item -> item.getQuantity() >= quantity));






                });

        Jwt jwt = createJwtToken("user");

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/purchases")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                        .content(String.valueOf(jsonPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

    }








}
