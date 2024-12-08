package com.eatcarefully.backend;


import com.eatcarefully.backend.controller.ProductController;
import com.eatcarefully.backend.dto.ScanResponseDTO;
import com.eatcarefully.backend.helper.ImageHelper;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private ImageHelper imageHelper;



    // product details

    @Test
    public void Should_ReturnProductDetails_When_ProductExists() throws Exception{

        String barcode = "11111111";
        ScanResponseDTO scanResponseDTO = new ScanResponseDTO(barcode, "testName", "testScore", "testBrand", "testUrl", null, null, null);

        when(productService.getProductDetails(any())).thenReturn(scanResponseDTO);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/products/{barcode}", barcode)
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect( jsonPath("$.id").value(barcode));

    }

    @Test
    public void Should_ReturnProductWithId0_When_ProductDoesNotExist() throws Exception{

        String barcode = "not_valid";

        when(productService.getProductDetails(any())).thenReturn(null);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/products/{barcode}", barcode)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect( jsonPath("$.id").value("0"));

    }











}
