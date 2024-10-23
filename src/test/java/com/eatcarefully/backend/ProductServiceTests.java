package com.eatcarefully.backend;

import com.eatcarefully.backend.model.Product;
import com.eatcarefully.backend.repository.ProductRepository;
import com.eatcarefully.backend.service.ProductJsonFactory;
import com.eatcarefully.backend.service.ProductService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import static org.mockito.Mockito.when;


@SpringJUnitConfig
@Import(ProductServiceTests.ProductServiceTestContextConfiguration.class)
public class ProductServiceTests {


    @Autowired
    private ProductService productService;

    @Mock
    private static ProductRepository productRepository;

    @Mock
    private static ProductJsonFactory productJsonFactory;

    private static final String VALID_BARCODE_NOT_IN_DB = "11111111";

    private static final String VALID_BARCODE_IN_DB = "22222222";
    private static final String INVALID_BARCODE = "notValid";

    @TestConfiguration
    static class ProductServiceTestContextConfiguration{


        @Bean
        public ProductService productService(){
            return new ProductService(productRepository, productJsonFactory);
        }


    }


    @BeforeEach
    public void setUp() throws NoSuchFieldException {

        Product product = new Product();
        product.setId(VALID_BARCODE_IN_DB);

        Optional<Product> emptyProductOptional = Optional.empty();
        Optional<Product> productOptional = Optional.of(product);
        when(productRepository.findById(INVALID_BARCODE)).thenReturn(emptyProductOptional);
        when(productRepository.findById(VALID_BARCODE_NOT_IN_DB)).thenReturn(emptyProductOptional);
        when(productRepository.findById(VALID_BARCODE_IN_DB)).thenReturn(productOptional);
        when(productJsonFactory.parseJSONToProduct(Mockito.any(JSONObject.class))).thenAnswer(
                new Answer<Product>(){

                    @Override
                    public Product answer(InvocationOnMock invocation) throws Throwable{

                        JSONObject json = invocation.getArgument(0, JSONObject.class);

                        if(json.has("code") && json.getString("code").equals(VALID_BARCODE_NOT_IN_DB)){

                            return product;
                        }
                        else
                            return null;

                    }

                });

    }


    // only fetching from database

    @Test
    public void Should_ReturnNull_When_ProductNotFoundInDatabase(){


        Product product = productService.getProductByBarcodeFromDatabase(INVALID_BARCODE);

        assertTrue(product == null);

    }


    @Test
    public void Should_ReturnProduct_When_ProductFoundInDatabase(){

        Product product = productService.getProductByBarcodeFromDatabase(VALID_BARCODE_IN_DB);

        assertFalse(product == null);
        assertTrue(product.getId().equals(VALID_BARCODE_IN_DB));


    }

    //fetching from database or OpenFoodFacts

    @Test
    public void Should_ReturnNull_When_ProductIsNotFoundInDatabaseNorOpenFoodFacts(){

        Product product = productService.getProductByBarcodeFromDatabaseOrOpenFoodFacts(INVALID_BARCODE);

        assertTrue(product == null);

    }


    @Test
    public void Should_ReturnProduct_When_ProductIsFoundInDatabaseNotOpenFoodFacts(){

        Product product = productService.getProductByBarcodeFromDatabaseOrOpenFoodFacts(VALID_BARCODE_IN_DB);

        assertFalse(product == null);
        assertTrue(product.getId().equals(VALID_BARCODE_IN_DB));

    }


    @Test
    public void Should_ReturnProduct_When_ProductIsNotFoundInDatabaseButInOpenFoodFacts(){

        Product product = productService.getProductByBarcodeFromDatabaseOrOpenFoodFacts(VALID_BARCODE_NOT_IN_DB);

        assertFalse(product == null);

    }













}
