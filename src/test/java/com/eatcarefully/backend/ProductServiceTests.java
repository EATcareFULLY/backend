package com.eatcarefully.backend;


import com.eatcarefully.backend.model.Allergen;
import com.eatcarefully.backend.model.Ingredient;
import com.eatcarefully.backend.model.Product;
import com.eatcarefully.backend.model.Tag;
import com.eatcarefully.backend.repository.AllergenRepository;
import com.eatcarefully.backend.repository.ProductRepository;
import com.eatcarefully.backend.repository.TagRepository;
import com.eatcarefully.backend.service.ProductService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;

@TestConfiguration
public class ProductServiceTests {

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private TagRepository tagRepository;

    @MockBean
    private AllergenRepository allergenRepository;
    

    public void setUp(){

        List<Tag> tags = List.of(
                new Tag(0L, "Not vegan"),
                new Tag(0L, "Not vegetarian"),
                new Tag(0L, "May contain gluten")

        );
        
        Tag tag = new Tag(0L,"Not vegan");

        List<Allergen> allergens = List.of(
                new Allergen(0L, "Gluten"),
                new Allergen(0L, "Peanuts")
        );

        List<Ingredient> ingredients = List.of(
                new Ingredient(1L, "Tomato Paste", "Concentrated tomato paste", 20.5f),
                new Ingredient(2L, "Sugar", "White granulated sugar", 10.0f),
                new Ingredient(3L, "Salt", "Table salt", 5.0f),
                new Ingredient(4L, "Vinegar", "Distilled white vinegar", 7.0f),
                new Ingredient(5L, "Spices", "Mixed spices", 1.5f),
                new Ingredient(6L, "Water", "Filtered water", 50.0f),
                new Ingredient(7L, "Garlic", "Minced garlic", 2.0f),
                new Ingredient(8L, "Onion", "Diced onion", 3.0f),
                new Ingredient(9L, "Oil", "Vegetable oil", 4.0f),
                new Ingredient(10L, "Lemon Juice", "Fresh lemon juice", 2.5f)
        );

         List<Product> products =  List.of(
                new Product("1L", "Ketchup", "C", "Brand",null, List.of(tags.get(0), tags.get(1)),allergens, List.of(ingredients.get(0), ingredients.get(1), ingredients.get(2), ingredients.get(3))),
                new Product("2L", "Mustard French", "B","Brand",null, List.of(tags.get(2), tags.get(3)),allergens, List.of(ingredients.get(4), ingredients.get(5), ingredients.get(6))),
                new Product("3L", "Mayonnaise Light", "A","Brand",null, List.of(tags.get(4), tags.get(1)),allergens, List.of(ingredients.get(7), ingredients.get(8), ingredients.get(9))),
                new Product("4L", "Barbecue Sauce","Brand",null, "B", List.of(tags.get(0), tags.get(2)),allergens, List.of(ingredients.get(1), ingredients.get(3), ingredients.get(5)))

        );


         Mockito.when(tagRepository.findAll()).thenReturn(tags);
         Mockito.when(tagRepository.findByName("TagDB")).thenReturn(Optional.ofNullable(tag));
        Mockito.when(tagRepository.findByName("TagNoDB")).thenReturn(Optional.ofNullable(null));

    }


//    @Test
//    public String formatApiStringShouldReturnNull(){
//
//
//        //given
//        String nullString = null;
//
//
//        //when
//
//
//
//        //then
//
//    }








}
