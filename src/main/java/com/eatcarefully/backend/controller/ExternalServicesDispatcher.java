package com.eatcarefully.backend.controller;


import com.eatcarefully.backend.dto.*;
import com.eatcarefully.backend.exceptions.DataNotFoundException;
import com.eatcarefully.backend.exceptions.ModelValidationException;
import com.eatcarefully.backend.exceptions.ServiceUnavailableException;
import com.eatcarefully.backend.helper.ImageHelper;
import com.eatcarefully.backend.helper.JwtHelper;
import com.eatcarefully.backend.model.Purchase;
import com.eatcarefully.backend.service.OCRService;
import com.eatcarefully.backend.service.PurchaseService;
import com.eatcarefully.backend.service.UserPreferenceAndNutritionalProfileService;
import com.eatcarefully.backend.service.external.IHistoryAnalysisClient;
import com.eatcarefully.backend.service.external.ILabelAnalysisClient;
import com.eatcarefully.backend.service.external.IRecommendationSystemClient;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.handler.timeout.TimeoutException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.net.http.HttpTimeoutException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/services")
@AllArgsConstructor
public class ExternalServicesDispatcher {

    private final int N_RECOMMENDATION_LIMIT = 3;
    private ILabelAnalysisClient labelAnalysisClient;
    private ImageHelper imageHelper;
    private OCRService ocrService;

    private PurchaseService purchaseService;

    private IHistoryAnalysisClient historyAnalysisClient;

    private JwtHelper jwtHelper;

    private UserPreferenceAndNutritionalProfileService preferencesService;

    private IRecommendationSystemClient recommendationSystemClient;


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ModelValidationException.class)
    public ResponseEntity<String> handleValidationException(ModelValidationException ex) {
        return ResponseEntity.unprocessableEntity().body(ex.getMessage());
    }


    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<String> handleConnectionFailure(ServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
    }

    @ExceptionHandler(HttpTimeoutException.class)
    public ResponseEntity<String> handleTimeout(HttpTimeoutException ex) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(ex.getMessage());
    }


    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<String> handleNoData(DataNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }



    // label analysis

    @PostMapping("/label-analysis/file")
    public Mono<JsonNode> handleLabelAnalysisRequestText(@RequestParam MultipartFile file) {

        if( file == null || file.isEmpty() || ! imageHelper.isFileExtensionSupported(file))
            return Mono.error(new IllegalArgumentException("Invalid file (null, empty or invalid extension"));

        String labelText = ocrService.extractTextFromFile(file);


            return labelAnalysisClient.submitLabelForAnalysis(labelText);
    }


    @PostMapping("/label-analysis/text")
    public Mono<JsonNode> handleLabelAnalysisRequestFile(@RequestBody LabelTextForAnalysisDTO dto) {


            return labelAnalysisClient.submitLabelForAnalysis(dto.getLabelText());

    }


    // history analysis

    @GetMapping("/history-analysis")
    public Mono<ResponseEntity<MultiValueMap<String, Object>>> handleHistoryAnalysisRequest(@AuthenticationPrincipal Jwt jwt, @RequestParam int year, @RequestParam int month){

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate endDay = yearMonth.atEndOfMonth();

        String username = jwtHelper.getUsernameFromToken(jwt);


        List<Purchase> purchases = purchaseService.getPurchasesBetween(username, firstDay, endDay );

        List<HistoryAnalysisProductDTO> products = purchases.stream()
                .map(Purchase::toListOfHistoryAnalysisProductDTO)
                .flatMap(List::stream).
                collect(Collectors.toList());

        NutritionalThresholdsDTO thresholds = preferencesService.getUserThresholds(username);

        if(purchases.isEmpty()  || thresholds == null)
            return Mono.error(new DataNotFoundException("No data for history analysis"));

        return historyAnalysisClient.submitProductsForHistoryAnalysis(new HistoryAnalysisRequestDTO(
                products,
                thresholds
        ));

    }

    // recommendation system

    @PostMapping("/recommendation-system")
    public Mono<String> handleSubmittingProductsForRecommendation(@AuthenticationPrincipal Jwt jwt, LocalDate date){

        String username = jwtHelper.getUsernameFromToken(jwt);

        String productBarcode = purchaseService.getBarcodeFromLeastHealthyProductPurchasedOn(username, date);

        if(productBarcode.isEmpty())
            throw new DataNotFoundException("No product purchased by user:" + username + " on " + date);

        List<UserPreferenceDTO> preferences = preferencesService.getUserPreferencesList(username);

        return recommendationSystemClient.submitProductsForRecommendation(new RecommendationRequestDTO(productBarcode, N_RECOMMENDATION_LIMIT, preferences));


    }


    @GetMapping("/recommendation-system")
    public Mono<JsonNode> getRecommendationsResult(String recommendationId){

        return recommendationSystemClient.getRecommendationResults(recommendationId);
    }







}
