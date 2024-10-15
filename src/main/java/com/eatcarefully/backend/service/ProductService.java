package com.eatcarefully.backend.service;

import com.eatcarefully.backend.model.Allergen;
import com.eatcarefully.backend.model.Ingredient;
import com.eatcarefully.backend.model.Product;
import com.eatcarefully.backend.model.Tag;
import com.eatcarefully.backend.repository.AllergenRepository;
import com.eatcarefully.backend.repository.IngredientRepository;
import com.eatcarefully.backend.repository.ProductRepository;
import com.eatcarefully.backend.repository.TagRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final TagRepository tagRepository;

    private final AllergenRepository allergenRepository;


    @Cacheable(cacheNames = "products", key = "#barcode")
    public ResponseEntity<?> getProductDetailsByBarcode(Long barcode) {
        Optional<Product> product = productRepository.findById(barcode); //TODO: maybe change to findProductByBarcode and leave ID auto generated
        if(product.isEmpty()) {


            try {
                JSONObject openFoodFactAPIResponse = getOpenFoodFactsAPIResponse(barcode);
                Product productFromJson = parseJSONToProduct(openFoodFactAPIResponse);

                if(productFromJson == null){
                    return ResponseEntity.ok(new Product());
                }
                else {
                    productRepository.save(productFromJson);
                    return ResponseEntity.ok(productFromJson);
                }


            } catch (IOException | InterruptedException | NoSuchFieldException e) {
                return ResponseEntity.notFound().build();
            }


        } else {
            return ResponseEntity.ok(product.get());
        }
    }


    private JSONObject getOpenFoodFactsAPIResponse(Long barcode) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        String url =
                "https://world.openfoodfacts.org/api/v3/product/"
        + barcode
        + "?fields=product_name,product_name_ar,product_name_cs,product_name_da,product_name_de,product_name_en,product_name_es,product_name_et,product_name_fiproduct_name_fr,product_name_it,product_name_lt,product_name_lv,product_name_no,product_name_pl,product_name_pt,product_name_ru,product_name_sv,product_name_zh,allergens_hierarchy,ingredients,brands,ingredients_analysis,nutriments,nutrient_levels,nutriscore,keywords,additive_original_tags,selected_images,ecoscore_data";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return new JSONObject(response.body());
    }

    private Boolean isProductFoundInOpenFoodFacts(JSONObject json){

        JSONObject result = json.optJSONObject("result");
        if(result != null){
            String resultId = result.optString("id");

            if(resultId.equals("product_found"))
                return true;
        }

        return false;

    }

    private Product parseJSONToProduct(JSONObject json) throws NoSuchFieldException {

        if(!isProductFoundInOpenFoodFacts(json)){
            return null;
        }

        JSONObject productObject = json.optJSONObject("product");

        if(productObject ==  null)
            throw new NoSuchFieldException();

        Long id = json.optLong("code");
        String name = getProductName(productObject);
        String brand = productObject.optString("brands");

        JSONObject imagesObject = productObject.optJSONObject("selected_images");
        String frontImageUrl = imagesObject != null ? getFrontDisplayImageURL(imagesObject) : null;

        JSONObject nutriscoreObject = productObject.optJSONObject("nutriscore");
        String nutriscore = nutriscoreObject != null ? getNutriscore(nutriscoreObject) : null;

        JSONArray ingredientsArray = productObject.optJSONArray("ingredients");
        List<Ingredient> ingredients = ingredientsArray != null ? getIngredientsList(ingredientsArray) : List.of();

        List<Tag> tags = getTags(productObject);

        List<Allergen> allergens = getAllergens(productObject);

        Product product = new Product(id, name,nutriscore, brand, frontImageUrl, tags,allergens, ingredients);



        return product;

    }

    private String getProductName(JSONObject productObject){

        //check for default name
        String name = productObject.optString("product_name");

        if(name != null  &&  !name.isEmpty())
            return name;

        //no default name -> check for english name
        name = productObject.optString("product_name_en");

        if(name != null  &&  !name.isEmpty())
            return name;

        // no default or english name -> check for any name

        List<String> productKeySet = productObject.keySet().stream().toList();

        for(String key : productKeySet){
            if(key.startsWith("product_name_")) {
                name = productObject.optString(key);
                if (name != null && !name.isEmpty())
                    break;
            }
        }
        return name;

    }

    private String getFrontDisplayImageURL(JSONObject imagesObject){


        JSONObject front = imagesObject.optJSONObject("front");
        if(front != null){
            JSONObject display = front.optJSONObject("display");
            if(display != null && ! display.keySet().isEmpty()){

                //get english version if possible
                if(display.keySet().contains("en")){
                    String imageUrl = display.optString("en");
                    return imageUrl;
                }
                //else get first
                else{

                    String imageUrl = display.optString(display.keySet().toArray()[0].toString());
                    return imageUrl;
                }
            }
        }
        return null;
    }

    private List<Allergen> getAllergens(JSONObject productObject){

        List<Allergen> allergens = new ArrayList<>();

        JSONArray allergensHierarchy = productObject.optJSONArray("allergens_hierarchy");

        if(allergensHierarchy != null && !allergensHierarchy.isEmpty()){

            for(int i = 0; i < allergensHierarchy.length(); i++){

                String name = formatApiString(allergensHierarchy.getString(i));
                allergens.add(findOrCreateAllergen(name));
            }
        }


        return allergens;
    }

    @Cacheable( cacheNames = "allergens", key = "#name")
    private Allergen findOrCreateAllergen(String name){

        Optional<Allergen> dbAllergen = allergenRepository.findByName(name);

        if(! dbAllergen.isPresent()){
            Allergen newAllergen = new Allergen();
            newAllergen.setName(name);
            allergenRepository.save(newAllergen);
            dbAllergen = allergenRepository.findByName(name);
        }

        return dbAllergen.get();


    }

    private List<Ingredient> getIngredientsList(JSONArray ingredientsArray){

        Map<String, Float> ingredientsMap = new HashMap<>();

        for( int i = 0; i < ingredientsArray.length(); i++){

            JSONObject object = ingredientsArray.getJSONObject(i);
            JSONArray innerIngredients = object.optJSONArray("ingredients");

            // if there are nested ingredients skip outer and add inner ingredients
            if(innerIngredients != null){
                for( int j = 0; j < innerIngredients.length(); j++){

                    JSONObject innerIngredient = innerIngredients.getJSONObject(j);

                    String name = formatApiString(innerIngredient.optString("id"));
                    Float content = innerIngredient.optFloat("percent_estimate");

                    if( ingredientsMap.containsKey(name))
                        ingredientsMap.put(name, ingredientsMap.get(name) + content);
                    else
                        ingredientsMap.put(name, content);
                }

            }
            // if there is no nested ingredients add outer ingredient
            else{
                String name = formatApiString(object.optString("id"));
                Float content = object.optFloat("percent_estimate");

                if( ingredientsMap.containsKey(name))
                    ingredientsMap.put(name, ingredientsMap.get(name) + content);
                else
                    ingredientsMap.put(name, content);

            }

        }

        // convert ingredients map to list
        List<Ingredient> ingredients = new ArrayList<>();

        ingredientsMap.forEach((name, content) -> {
            Ingredient ingredient = new Ingredient();
            ingredient.setName(name);
            ingredient.setContent(content);

            ingredients.add(ingredient);

        });


        return ingredients;
    }


    private String getNutriscore(JSONObject nutriscoreObject){

        String latestYear = null;
        String latestGrade = null;

        for (String year : nutriscoreObject.keySet()) {
            JSONObject yearObject =  nutriscoreObject.getJSONObject(year);
            String grade = yearObject.getString("grade");

            if (latestYear == null || year.compareTo(latestYear) > 0) {
                latestYear = year;
                latestGrade = grade;
            }
        }

        return latestGrade;
    }


    private List<Tag> getTags(JSONObject productObject){

        List<Tag> tags = new ArrayList<Tag>();

        JSONObject ingredientAnalysisObject = getNestedObject(productObject, List.of(
                "ingredients_analysis"
        ));

        if(ingredientAnalysisObject != null){

            Set<String> ingredientAnalysisKeySet = ingredientAnalysisObject.keySet();
            log.info(ingredientAnalysisKeySet.toString());

            // vegan
            // no fields non-vegan and vegan-status-unknown

            if(!ingredientAnalysisKeySet.contains("en:non-vegan") && !ingredientAnalysisKeySet.contains("en:vegan-status-unknown")){
                tags.add(findOrCreateTag("Vegan"));
            }


            //vegetarian
            if(!ingredientAnalysisKeySet.contains("en:non-vegetarian") && !ingredientAnalysisKeySet.contains("en:vegetarian-status-unknown")){
                tags.add(findOrCreateTag("Vegetarian"));
            }


            //palm oil

            if(ingredientAnalysisKeySet.contains("en:palm-oil")){
                tags.add(findOrCreateTag("Contains palm oil"));
            }
            else{
                if(ingredientAnalysisKeySet.contains("en:palm-oil-content-unknown")){
                    tags.add(findOrCreateTag("May contain palm oil"));
                }
            }


        }

        //gluten

        JSONArray allergensHierarchy = productObject.optJSONArray("allergens_hierarchy");

        if(allergensHierarchy != null && !allergensHierarchy.isEmpty()){

            Boolean isGlutenFree = true;

           for(int i = 0; i< allergensHierarchy.length(); i++){
               String allergen = allergensHierarchy.optString(i);
               if(allergen.contains("gluten")){
                   isGlutenFree = false;
                   break;
               }
           }

           if(isGlutenFree)
               tags.add(findOrCreateTag("Gluten free"));

        }


        //eco packaging

            JSONObject packagingObject = getNestedObject(productObject, List.of(
                    "ecoscore_data", "adjustments", "packaging"
            ));

            String isNotEco = packagingObject.optString("non_recyclable_and_non_biodegradable_materials");

            if( isNotEco != null && isNotEco.equals("0")){

                tags.add(findOrCreateTag("Eco packaging"));
            }


        return tags;

    }


    //ugly as hell
    private JSONObject getNestedObject(JSONObject object, List<String> keys){

        if(object == null || keys.isEmpty())
            return null;

        // get first
        JSONObject temp = object.optJSONObject(keys.get(0));

        for(int i = 1; i <= keys.size(); i++){

            if(temp == null)
                break;

            if(i == keys.size()){
                return temp;
            }

            temp = temp.optJSONObject(keys.get(i));

        }

        return null;
    }





    @Cacheable(cacheNames = "tags", key="#name")
    private Tag findOrCreateTag(String name){

        Optional<Tag> dbTag = tagRepository.findByName(name);

        if(! dbTag.isPresent()){
            Tag newTag = new Tag();
            newTag.setName(name);
            tagRepository.save(newTag);
            dbTag = tagRepository.findByName(name);
        }

        return dbTag.get();


    }

    //remove 'en:' and replace '-' with ' '
    private String formatApiString(String string){

        if(string == null || string.isEmpty())
            return null;

        if(string.contains(":")){
            String[] temp = string.split(":");
            if(temp.length > 1)
                string = temp[1];
        }

        if(string.contains("-")){
            string = string.replace("-", " ");
        }

        string = string.substring(0,1).toUpperCase() + string.substring(1);

        return string;

    }

}


