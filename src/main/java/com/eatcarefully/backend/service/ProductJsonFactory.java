package com.eatcarefully.backend.service;

import com.eatcarefully.backend.model.Allergen;
import com.eatcarefully.backend.model.Ingredient;
import com.eatcarefully.backend.model.Product;
import com.eatcarefully.backend.model.Tag;
import lombok.AllArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@AllArgsConstructor
public class ProductJsonFactory {


    private TagService tagService;
    private AllergenService allergenService;


    public Product parseJSONToProduct(JSONObject json) throws NoSuchFieldException {

        if(json.isEmpty()){
            return null;
        }

        JSONObject productObject = json.optJSONObject("product");

        if(productObject ==  null)
            throw new NoSuchFieldException();

        String id = json.optString("code");
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


    private List<Allergen> getAllergens(JSONObject productObject){

        List<Allergen> allergens = new ArrayList<>();

        JSONArray allergensHierarchy = productObject.optJSONArray("allergens_hierarchy");

        if(allergensHierarchy != null && !allergensHierarchy.isEmpty()){

            for(int i = 0; i < allergensHierarchy.length(); i++){

                String name = ProductJSONUtils.formatApiString(allergensHierarchy.getString(i));
                allergens.add(allergenService.findOrCreateAllergen(name));
            }
        }


        return allergens;
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

        JSONObject ingredientAnalysisObject = ProductJSONUtils.getNestedObject(productObject, List.of(
                "ingredients_analysis"
        ));

        if(ingredientAnalysisObject != null){

            Set<String> ingredientAnalysisKeySet = ingredientAnalysisObject.keySet();
            //log.info(ingredientAnalysisKeySet.toString());

            // vegan
            // no fields non-vegan and vegan-status-unknown

            if(!ingredientAnalysisKeySet.contains("en:non-vegan") && !ingredientAnalysisKeySet.contains("en:vegan-status-unknown")){
                tags.add(tagService.findOrCreateTag("Vegan"));
            }


            //vegetarian
            if(!ingredientAnalysisKeySet.contains("en:non-vegetarian") && !ingredientAnalysisKeySet.contains("en:vegetarian-status-unknown")){
                tags.add(tagService.findOrCreateTag("Vegetarian"));
            }


            //palm oil

            if(ingredientAnalysisKeySet.contains("en:palm-oil")){
                tags.add(tagService.findOrCreateTag("Contains palm oil"));
            }
            else{
                if(ingredientAnalysisKeySet.contains("en:palm-oil-content-unknown")){
                    tags.add(tagService.findOrCreateTag("May contain palm oil"));
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
                tags.add(tagService.findOrCreateTag("Gluten free"));

        }


        //eco packaging

        JSONObject packagingObject = ProductJSONUtils.getNestedObject(productObject, List.of(
                "ecoscore_data", "adjustments", "packaging"
        ));

        String isNotEco = packagingObject.optString("non_recyclable_and_non_biodegradable_materials");

        if( isNotEco != null && isNotEco.equals("0")){

            tags.add(tagService.findOrCreateTag("Eco packaging"));
        }


        return tags;

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

                    String name = ProductJSONUtils.formatApiString(innerIngredient.optString("id"));
                    Float content = innerIngredient.optFloat("percent_estimate");

                    if( ingredientsMap.containsKey(name))
                        ingredientsMap.put(name, ingredientsMap.get(name) + content);
                    else
                        ingredientsMap.put(name, content);
                }

            }
            // if there is no nested ingredients add outer ingredient
            else{
                String name = ProductJSONUtils.formatApiString(object.optString("id"));
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




}
