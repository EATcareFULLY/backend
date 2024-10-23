package com.eatcarefully.backend;


import com.eatcarefully.backend.controller.ProductController;
import com.eatcarefully.backend.model.Product;
import com.eatcarefully.backend.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;



    // product details

    @Test
    public void Should_ReturnProductDetails_When_ProductExists() throws Exception{

        String barcode = "11111111";
        Product product = new Product(barcode, "test","test","test","test", null, null, null);

        when(productService.getProductByBarcodeFromDatabaseOrOpenFoodFacts(barcode)).thenReturn(product);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/products/{barcode}", barcode)
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect( jsonPath("$.id").value(barcode));

    }

    @Test
    public void Should_ReturnProductWithId0_When_ProductDoesNotExist() throws Exception{

        String barcode = "not_valid";

        when(productService.getProductByBarcodeFromDatabaseOrOpenFoodFacts(barcode)).thenReturn(null);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/products/{barcode}", barcode)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect( jsonPath("$.id").value("0"));

    }


    // get recommendations

    @Test
    public void Should_ReturnProductRecommendations_When_ProductExists() throws Exception{

        String barcode = "11111111";
        Product product = new Product(barcode, "test","test","test","test", null, null, null);

        when(productService.getProductByBarcodeFromDatabase(barcode)).thenReturn(product);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/products/{barcode}/recommend", barcode)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect( jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)));

    }

    @Test
    public void Should_ReturnRecommendationsNotFound_When_ProductDoesNotExists() throws Exception{

        String barcode = "not_valid";

        when(productService.getProductByBarcodeFromDatabase(barcode)).thenReturn(null);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/products/{barcode}/recommend", barcode)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isNotFound());

    }





    // scan label

    @Test
    public void Should_ReturnBadRequest_When_EvalLabelTextIsEmpty() throws Exception{

        String jsonPayload = "{\"labelText\": \"\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.post("/products/scan-label")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .content(jsonPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void Should_ReturnBadRequest_When_EvalLabelTextIsBlank() throws Exception{

        String jsonPayload = "{\"labelText\": \"  \"}";
        this.mockMvc.perform(MockMvcRequestBuilders.post("/products/scan-label")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .content(jsonPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void Should_ReturnOk_When_EvalLabelTextIsOk() throws Exception{

        String jsonPayload = "{\"labelText\": \"testing\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.post("/products/scan-label")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .content(jsonPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("testing")));
    }




}
