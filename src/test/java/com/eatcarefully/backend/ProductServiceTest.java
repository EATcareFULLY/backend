package com.eatcarefully.backend;

import com.eatcarefully.backend.model.Product;
import com.eatcarefully.backend.repository.ProductRepository;
import com.eatcarefully.backend.service.ProductJsonFactory;
import com.eatcarefully.backend.service.ProductService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringJUnitConfig
public class ProductServiceTest {

    private static final String VALID_BARCODE_NOT_IN_DB = "11111111";

    private static final String VALID_BARCODE_IN_DB = "22222222";
    private static final String INVALID_BARCODE = "notValid";

    @InjectMocks
    private ProductService productService;

    @Mock
    private static ProductRepository productRepository;

    @Mock
    private static ProductJsonFactory productJsonFactory;



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

        assertNull(product);

    }


    @Test
    public void Should_ReturnProduct_When_ProductFoundInDatabase(){

        Product product = productService.getProductByBarcodeFromDatabase(VALID_BARCODE_IN_DB);

        assertNotNull(product);
        assertEquals(VALID_BARCODE_IN_DB, product.getId());


    }

    //fetching from database or OpenFoodFacts

    @Test
    public void Should_ReturnNull_When_ProductIsNotFoundInDatabaseNorOpenFoodFacts(){

        Product product = productService.getProductByBarcodeFromDatabaseOrOpenFoodFacts(INVALID_BARCODE);

        assertNull(product);

    }


    @Test
    public void Should_ReturnProduct_When_ProductIsFoundInDatabaseNotOpenFoodFacts(){

        Product product = productService.getProductByBarcodeFromDatabaseOrOpenFoodFacts(VALID_BARCODE_IN_DB);

        assertNotNull(product);
        assertEquals(VALID_BARCODE_IN_DB, product.getId());

    }


    @Test
    public void Should_ReturnProduct_When_ProductIsNotFoundInDatabaseButInOpenFoodFacts(){

        Product product = productService.getProductByBarcodeFromDatabaseOrOpenFoodFacts(VALID_BARCODE_NOT_IN_DB);

        assertNotNull(product);

    }













}
