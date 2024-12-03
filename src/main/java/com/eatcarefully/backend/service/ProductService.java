package com.eatcarefully.backend.service;

import com.eatcarefully.backend.dto.ScanResponseDTO;
import com.eatcarefully.backend.helper.ProductJsonFactory;
import com.eatcarefully.backend.model.Allergen;
import com.eatcarefully.backend.model.Ingredient;
import com.eatcarefully.backend.model.Product;
import com.eatcarefully.backend.model.Tag;
import com.eatcarefully.backend.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
@EnableAspectJAutoProxy(exposeProxy = true)
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductJsonFactory productJsonFactory;


    //it stays

    // not for processing json response

    // this method will try to fetch product from OpenFoodFacts if not found in database
    @Cacheable(cacheNames = "products", key = "#barcode")
    public ScanResponseDTO getProductDetails(String barcode) {
        Optional<Product> product = productRepository.findById(barcode);
        if(product.isEmpty()) {


            try {
                JSONObject openFoodFactAPIResponse = getOpenFoodFactsAPIResponse(barcode);
                Product productFromJson = productJsonFactory.parseJSONToProduct(openFoodFactAPIResponse);

                if(productFromJson == null){
                    return null;
                }
                else {
                    productRepository.save(productFromJson);
                    return toScanResponseDTO(productFromJson);
                }


            } catch (IOException | InterruptedException | NoSuchFieldException e) {
                return null;
            }


        } else {
            return toScanResponseDTO(product.get());
        }
    }


    //not staying

    private JSONObject getOpenFoodFactsAPIResponse(String barcode) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        String url =
                "https://world.openfoodfacts.org/api/v3/product/"
        + barcode
                        + "?fields=product_name,product_name_ar,product_name_cs,product_name_da,product_name_de,product_name_en,product_name_es,product_name_et,product_name_fiproduct_name_fr,product_name_it,product_name_lt,product_name_lv,product_name_no,product_name_pl,product_name_pt,product_name_ru,product_name_sv,product_name_zh,categories,allergens_hierarchy,ingredients,brands,ingredients_analysis,nutriments,nutrient_levels,nutriscore,nova_group,keywords,additive_original_tags,selected_images,ecoscore_data";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return new JSONObject(response.body());
    }


    // not staying
    private Boolean isProductFoundInOpenFoodFacts(JSONObject json){

        JSONObject result = json.optJSONObject("result");
        if(result != null){
            String resultId = result.optString("id");

            return resultId.equals("product_found");
        }

        return false;

    }


    // this method

    public Product getProductByBarcodeFromDatabase(String barcode){

        Optional<Product> product = productRepository.findById(barcode);

        return product.orElse(null);

    }


    private ScanResponseDTO toScanResponseDTO(Product product) {
        return new ScanResponseDTO(
                product.getId(),
                product.getName(),
                product.getScore(),
                product.getBrand(),
                product.getImageURL(),
                product.getTags().stream().map(Tag::toDTO).toList(),
                product.getAllergens().stream().map(Allergen::toDTO).toList(),
                product.getIngredients().stream().map(Ingredient::toDTO).toList()
        );
    }




}


