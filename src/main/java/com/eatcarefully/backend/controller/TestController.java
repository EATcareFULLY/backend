package com.eatcarefully.backend.controller;

import com.eatcarefully.backend.model.Ingredient;
import com.eatcarefully.backend.model.Product;
import com.eatcarefully.backend.model.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/test")
public class TestController {

    @GetMapping(path = "/hello")
    public ResponseEntity<String> helloWorld(){
        return ResponseEntity.ok("Hello there");
    }

    @GetMapping(path = "/products")
    public ResponseEntity<List<Product>> getTestProducts(){
        List<Product> returnList = generateListOfProducts();
        return ResponseEntity.ok(returnList);
    }



    private List<Product> generateListOfProducts() {
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

        List<Tag> tags = List.of(
                new Tag(1L, "Organic"),
                new Tag(2L, "Vegan"),
                new Tag(3L, "Gluten-Free"),
                new Tag(4L, "Non-GMO"),
                new Tag(5L, "Low Sugar")
        );

        return List.of(
                new Product(1L, "Ketchup", "C", List.of(tags.get(0), tags.get(1)), List.of(ingredients.get(0), ingredients.get(1), ingredients.get(2), ingredients.get(3))),
                new Product(2L, "Mustard French", "B", List.of(tags.get(2), tags.get(3)), List.of(ingredients.get(4), ingredients.get(5), ingredients.get(6))),
                new Product(3L, "Mayonnaise Light", "A", List.of(tags.get(4), tags.get(1)), List.of(ingredients.get(7), ingredients.get(8), ingredients.get(9))),
                new Product(4L, "Barbecue Sauce", "B", List.of(tags.get(0), tags.get(2)), List.of(ingredients.get(1), ingredients.get(3), ingredients.get(5))),
                new Product(5L, "Hot Sauce", "C", List.of(tags.get(1), tags.get(3)), List.of(ingredients.get(0), ingredients.get(2), ingredients.get(4))),
                new Product(6L, "Salad Dressing", "A", List.of(tags.get(4), tags.get(2)), List.of(ingredients.get(6), ingredients.get(8), ingredients.get(9))),
                new Product(7L, "Soy Sauce", "B", List.of(tags.get(0), tags.get(4)), List.of(ingredients.get(7), ingredients.get(1), ingredients.get(3))),
                new Product(8L, "Teriyaki Sauce", "A", List.of(tags.get(1), tags.get(2)), List.of(ingredients.get(0), ingredients.get(5), ingredients.get(9))),
                new Product(9L, "Pesto Sauce", "C", List.of(tags.get(3), tags.get(0)), List.of(ingredients.get(4), ingredients.get(8), ingredients.get(7))),
                new Product(10L, "Ranch Dressing", "B", List.of(tags.get(2), tags.get(1)), List.of(ingredients.get(2), ingredients.get(3), ingredients.get(6))),
                new Product(11L, "Caesar Dressing", "A", List.of(tags.get(4), tags.get(0)), List.of(ingredients.get(1), ingredients.get(9), ingredients.get(0))),
                new Product(12L, "Thousand Island", "B", List.of(tags.get(3), tags.get(2)), List.of(ingredients.get(5), ingredients.get(7), ingredients.get(8))),
                new Product(13L, "Tartar Sauce", "C", List.of(tags.get(1), tags.get(4)), List.of(ingredients.get(0), ingredients.get(2), ingredients.get(4))),
                new Product(14L, "Honey Mustard", "B", List.of(tags.get(2), tags.get(3)), List.of(ingredients.get(6), ingredients.get(1), ingredients.get(3))),
                new Product(15L, "Buffalo Sauce", "A", List.of(tags.get(4), tags.get(1)), List.of(ingredients.get(8), ingredients.get(7), ingredients.get(2))),
                new Product(16L, "Sriracha Sauce", "C", List.of(tags.get(0), tags.get(2)), List.of(ingredients.get(5), ingredients.get(9), ingredients.get(6))),
                new Product(17L, "Tzatziki Sauce", "B", List.of(tags.get(3), tags.get(0)), List.of(ingredients.get(4), ingredients.get(7), ingredients.get(0))),
                new Product(18L, "Marinara Sauce", "A", List.of(tags.get(1), tags.get(4)), List.of(ingredients.get(1), ingredients.get(3), ingredients.get(8))),
                new Product(19L, "Salsa", "C", List.of(tags.get(2), tags.get(3)), List.of(ingredients.get(6), ingredients.get(9), ingredients.get(0))),
                new Product(20L, "Guacamole", "A", List.of(tags.get(4), tags.get(1)), List.of(ingredients.get(2), ingredients.get(5), ingredients.get(8)))
        );
    }


}
