package com.eatcarefully.backend.controller;

import com.eatcarefully.backend.model.*;
import com.eatcarefully.backend.repository.AllergenRepository;
import com.eatcarefully.backend.repository.IngredientRepository;
import com.eatcarefully.backend.repository.ProductRepository;
import com.eatcarefully.backend.repository.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/test")
@AllArgsConstructor
public class TestController {

    private ProductRepository productRepository;

    private IngredientRepository ingredientRepository;

    private TagRepository tagRepository;

    private AllergenRepository allergenRepository;

    @GetMapping(path = "/hello")
    public ResponseEntity<String> helloWorld(){
        return ResponseEntity.ok("Hello there");
    }

    @GetMapping(path = "/secure")
    public ResponseEntity<String> secureHelloWorld(){
        return ResponseEntity.ok("Hello from secure endpoint");
    }

    // have to decide if we need have role based access control
    @GetMapping(path = "/helloAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> helloAdmin(){
        return ResponseEntity.ok("Hello there admin");
    }

    @GetMapping(path = "/products")
    public ResponseEntity<List<Product>> getTestProducts(){
        List<Product> returnList = generateListOfProducts();
        return ResponseEntity.ok(returnList);
    }

    @GetMapping(path = "/db/products/all")
    public ResponseEntity<List<Product>> getTestProductsDB(){
        List<Product> returnList = productRepository.findAll().stream().toList();
        return ResponseEntity.ok(returnList);
    }

    @GetMapping(path = "/db/ingredients/all")
    public ResponseEntity<List<Ingredient>> getTestIngredientsDB(){
        List<Ingredient> returnList = ingredientRepository.findAll().stream().toList();
        return ResponseEntity.ok(returnList);
    }

    @GetMapping(path = "/db/tags/all")
    public ResponseEntity<List<Tag>> getTagsDB(){
        List<Tag> returnList = tagRepository.findAll().stream().toList();
        return ResponseEntity.ok(returnList);
    }

    @GetMapping(path = "/db/allergens/all")
    public ResponseEntity<List<Allergen>> getAllergensDB(){
        List<Allergen> returnList = allergenRepository.findAll().stream().toList();
        return ResponseEntity.ok(returnList);
    }

    @GetMapping(path = "/purchases")
    public ResponseEntity<List<Purchase>> getTestPurchases(){
        List<Purchase> mockPurchases = new ArrayList<>();
        List<Product> products = generateListOfProducts();

        for (int i = 0; i < 20; i++) {
            Purchase purchase = new Purchase((long) i, "user", LocalDate.now(),  List.of());
            mockPurchases.add(purchase);
        }
        return ResponseEntity.ok(mockPurchases);
    }

    @GetMapping(path = "/product")
    public ResponseEntity<Product> getTestProduct(){

        List<Ingredient> ingredients = List.of(
                new Ingredient(18066L, "water", "PLACEHOLDER - no desc yet", 21.75f),
                new Ingredient(9100L, "long-grain-rice", "PLACEHOLDER - no desc yet", 7.25f),
                new Ingredient(18066L, "water", "PLACEHOLDER - no desc yet", 12.65f),
                new Ingredient(20034L, "onion", "PLACEHOLDER - no desc yet", 5.175f),
                new Ingredient(0L, "rapeseed-oil", "PLACEHOLDER - no desc yet", 2.5875f),
                new Ingredient(11000L, "garlic-puree", "PLACEHOLDER - no desc yet", 1.29375f),
                new Ingredient(2028L, "lemon-juice-from-concentrate", "PLACEHOLDER - no desc yet", 0.646875f),
                new Ingredient(11014L, "parsley", "PLACEHOLDER - no desc yet", 0.3234375f),
                new Ingredient(11049L, "paprika", "PLACEHOLDER - no desc yet", 0.16171875f),
                new Ingredient(0L, "maltodextrin", "PLACEHOLDER - no desc yet", 0.0404296875f),
                new Ingredient(0L, "yeast-extract", "PLACEHOLDER - no desc yet", 0.02021484375f),
                new Ingredient(31016L, "sugar", "PLACEHOLDER - no desc yet", 0.010107421875f),
                new Ingredient(11058L, "salt", "PLACEHOLDER - no desc yet", 0.0050537109375f),
                new Ingredient(20009L, "carrot", "PLACEHOLDER - no desc yet", 0.00252685546875f),
                new Ingredient(20034L, "onion", "PLACEHOLDER - no desc yet", 0.001263427734375f),
                new Ingredient(20047L, "tomato", "PLACEHOLDER - no desc yet", 0.001263427734375f),
                new Ingredient(0L, "vegetable", "PLACEHOLDER - no desc yet", 0.0050537109375f),
                new Ingredient(0L, "e415", "PLACEHOLDER - no desc yet", 0.0404296875f),
                new Ingredient(11015L, "black-pepper", "PLACEHOLDER - no desc yet", 0.0404296875f),
                new Ingredient(0L, "chicken", "PLACEHOLDER - no desc yet", 20.765f),
                new Ingredient(11058L, "salt", "PLACEHOLDER - no desc yet", 0.234999999999999f),
                new Ingredient(0L, "pea", "PLACEHOLDER - no desc yet", 8.0f),
                new Ingredient(20047L, "tomato", "PLACEHOLDER - no desc yet", 4.545f),
                new Ingredient(17440L, "sunflower-oil", "PLACEHOLDER - no desc yet", 1.2275f),
                new Ingredient(11058L, "salt", "PLACEHOLDER - no desc yet", 0.235f),
                new Ingredient(11000L, "garlic", "PLACEHOLDER - no desc yet", 0.235f),
                new Ingredient(11035L, "oregano", "PLACEHOLDER - no desc yet", 0.7575f),
                new Ingredient(20087L, "red-bell-pepper", "PLACEHOLDER - no desc yet", 7.0f),
                new Ingredient(0L, "pork", "PLACEHOLDER - no desc yet", 2.69230769230769f),
                new Ingredient(16530L, "pork-fat", "PLACEHOLDER - no desc yet", 1.15384615384615f),
                new Ingredient(0L, "pork-rind", "PLACEHOLDER - no desc yet", 0.576923076923077f),
                new Ingredient(11049L, "paprika", "PLACEHOLDER - no desc yet", 0.288461538461538f),
                new Ingredient(11058L, "salt", "PLACEHOLDER - no desc yet", 0.144230769230769f),
                new Ingredient(0L, "sodium-nitrite", "PLACEHOLDER - no desc yet", 0.0721153846153846f),
                new Ingredient(0L, "spices", "PLACEHOLDER - no desc yet", 0.0360576923076925f),
                new Ingredient(0L, "spice-extract", "PLACEHOLDER - no desc yet", 0.0180288461538463f),
                new Ingredient(0L, "glucono-delta-lactone", "PLACEHOLDER - no desc yet", 0.00901442307692335f),
                new Ingredient(31016L, "dextrose", "PLACEHOLDER - no desc yet", 0.00450721153846168f),
                new Ingredient(11000L, "garlic", "PLACEHOLDER - no desc yet", 0.00225360576923084f),
                new Ingredient(0L, "sodium-ascorbate", "PLACEHOLDER - no desc yet", 0.00112680288461542f),
                new Ingredient(11048L, "nutmeg", "PLACEHOLDER - no desc yet", 0.00112680288461497f)

        );

        Product product = generateTestProductFromIngredients(ingredients);


        return ResponseEntity.ok(product);
    }

    private static Product generateTestProductFromIngredients(List<Ingredient> ingredients) {
        List<Tag> tags = List.of(
                new Tag(0L, "Not vegan"),
                new Tag(0L, "Not vegetarian"),
                new Tag(0L, "May contain gluten")

        );

        List<Allergen> allergens = List.of(
                new Allergen(0L, "Gluten"),
                new Allergen(0L, "Peanuts")
        );

        String ingredientId = "55904223289";

        List<Category> categories = List.of(
          new Category(0L, "Frozen foods")
        );


        //score is nutriscore
        return new Product(
                ingredientId,
                "High Protein Chicken & Chorizo Paella",
                "B",
                "2",
                "Muscle Foood",
                "https://images.openfoodfacts.org/images/products/505/590/422/3289/front_en.3.400.jpg",
                tags,
                allergens,
                ingredients,
                categories
        );
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

        List<Allergen> allergens = List.of(
                new Allergen(0L, "Gluten"),
                new Allergen(0L, "Peanuts")
        );

        List<Category> condiments = List.of(
                new Category(1L, "Condiments"),
                new Category(2L, "Sauces"),
                new Category(3L, "Dressings"),
                new Category(4L, "Asian cuisine"),
                new Category(5L, "Mexican cuisine"),
                new Category(6L, "Mediterranean cuisine")
        );

        return List.of(
                new Product("1L", "Ketchup", "C", "3", "Brand", null,
                        List.of(tags.get(0), tags.get(1)), allergens,
                        List.of(ingredients.get(0), ingredients.get(1), ingredients.get(2), ingredients.get(3)),
                        List.of(condiments.get(0), condiments.get(1))),

                new Product("2L", "Mustard French", "B", "2", "Brand", null,
                        List.of(tags.get(2), tags.get(3)), allergens,
                        List.of(ingredients.get(4), ingredients.get(5), ingredients.get(6)),
                        List.of(condiments.get(0))),

                new Product("3L", "Mayonnaise Light", "A", "2", "Brand", null,
                        List.of(tags.get(4), tags.get(1)), allergens,
                        List.of(ingredients.get(7), ingredients.get(8), ingredients.get(9)),
                        List.of(condiments.get(0), condiments.get(2))),

                new Product("4L", "Barbecue Sauce", "B", "4", "Brand", null,
                        List.of(tags.get(0), tags.get(2)), allergens,
                        List.of(ingredients.get(1), ingredients.get(3), ingredients.get(5)),
                        List.of(condiments.get(1))),

                new Product("5L", "Hot Sauce", "C", "3", "Brand", null,
                        List.of(tags.get(1), tags.get(3)), allergens,
                        List.of(ingredients.get(0), ingredients.get(2), ingredients.get(4)),
                        List.of(condiments.get(1))),

                new Product("6L", "Salad Dressing", "A", "3", "Brand", null,
                        List.of(tags.get(4), tags.get(2)), allergens,
                        List.of(ingredients.get(6), ingredients.get(8), ingredients.get(9)),
                        List.of(condiments.get(2))),

                new Product("7L", "Soy Sauce", "B", "1", "Brand", null,
                        List.of(tags.get(0), tags.get(4)), allergens,
                        List.of(ingredients.get(7), ingredients.get(1), ingredients.get(3)),
                        List.of(condiments.get(1), condiments.get(3))),

                new Product("8L", "Teriyaki Sauce", "A", "4", "Brand", null,
                        List.of(tags.get(1), tags.get(2)), allergens,
                        List.of(ingredients.get(0), ingredients.get(5), ingredients.get(9)),
                        List.of(condiments.get(1), condiments.get(3))),

                new Product("9L", "Pesto Sauce", "C", "1", "Brand", null,
                        List.of(tags.get(3), tags.get(0)), allergens,
                        List.of(ingredients.get(4), ingredients.get(8), ingredients.get(7)),
                        List.of(condiments.get(1), condiments.get(5))),

                new Product("10L", "Ranch Dressing", "B", "4", "Brand", null,
                        List.of(tags.get(2), tags.get(1)), allergens,
                        List.of(ingredients.get(2), ingredients.get(3), ingredients.get(6)),
                        List.of(condiments.get(2))),

                new Product("11L", "Caesar Dressing", "A", "4", "Brand", null,
                        List.of(tags.get(4), tags.get(0)), allergens,
                        List.of(ingredients.get(1), ingredients.get(9), ingredients.get(0)),
                        List.of(condiments.get(2))),

                new Product("12L", "Thousand Island", "B", "4", "Brand", null,
                        List.of(tags.get(3), tags.get(2)), allergens,
                        List.of(ingredients.get(5), ingredients.get(7), ingredients.get(8)),
                        List.of(condiments.get(2))),

                new Product("13L", "Tartar Sauce", "C", "3", "Brand", null,
                        List.of(tags.get(1), tags.get(4)), allergens,
                        List.of(ingredients.get(0), ingredients.get(2), ingredients.get(4)),
                        List.of(condiments.get(0), condiments.get(1))),

                new Product("14L", "Honey Mustard", "B", "3", "Brand", null,
                        List.of(tags.get(2), tags.get(3)), allergens,
                        List.of(ingredients.get(6), ingredients.get(1), ingredients.get(3)),
                        List.of(condiments.get(0), condiments.get(1))),

                new Product("15L", "Buffalo Sauce", "A", "3", "Brand", null,
                        List.of(tags.get(4), tags.get(1)), allergens,
                        List.of(ingredients.get(8), ingredients.get(7), ingredients.get(2)),
                        List.of(condiments.get(1))),

                new Product("16L", "Sriracha Sauce", "C", "2", "Brand", null,
                        List.of(tags.get(0), tags.get(2)), allergens,
                        List.of(ingredients.get(5), ingredients.get(9), ingredients.get(6)),
                        List.of(condiments.get(1), condiments.get(3))),

                new Product("17L", "Tzatziki Sauce", "B", "1", "Brand", null,
                        List.of(tags.get(3), tags.get(0)), allergens,
                        List.of(ingredients.get(4), ingredients.get(7), ingredients.get(0)),
                        List.of(condiments.get(1), condiments.get(5))),

                new Product("18L", "Marinara Sauce", "A", "2", "Brand", null,
                        List.of(tags.get(1), tags.get(4)), allergens,
                        List.of(ingredients.get(1), ingredients.get(3), ingredients.get(8)),
                        List.of(condiments.get(1), condiments.get(5))),

                new Product("19L", "Salsa", "C", "1", "Brand", null,
                        List.of(tags.get(2), tags.get(3)), allergens,
                        List.of(ingredients.get(6), ingredients.get(9), ingredients.get(0)),
                        List.of(condiments.get(1), condiments.get(5))),

                new Product("20L", "Guacamole", "A", "1", "Brand", null,
                        List.of(tags.get(4), tags.get(1)), allergens,
                        List.of(ingredients.get(2), ingredients.get(5), ingredients.get(8)),
                        List.of(condiments.get(0), condiments.get(4)))
        );
    }


}
