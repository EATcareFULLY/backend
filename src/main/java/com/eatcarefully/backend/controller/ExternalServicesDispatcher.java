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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.net.http.HttpTimeoutException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


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
    public Mono<ResponseEntity<ByteArrayResource>> handleHistoryAnalysisRequest(@AuthenticationPrincipal Jwt jwt, @RequestParam int year, @RequestParam int month){

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
                month,
                year,
                products,
                thresholds
        ));

    }

    // recommendation system

    @PostMapping("/recommendation-system")
    @Operation(summary = "Get product recommendations based on user's least healthy purchase")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved recommendations")
    @ApiResponse(responseCode = "404", description = "No purchases found for today")
    @ApiResponse(responseCode = "503", description = "Recommendation service unavailable")
    public Mono<JsonNode> handleSubmittingProductsForRecommendation(@AuthenticationPrincipal Jwt jwt) {

        String username = jwtHelper.getUsernameFromToken(jwt);
        logger.debug("Processing recommendation request for user: {}", username);
        LocalDate today = LocalDate.now();

        String productBarcode = purchaseService.getBarcodeFromLeastHealthyProductPurchasedOn(username, today);
        if (Objects.isNull(productBarcode) || productBarcode.isEmpty()) {
            throw new DataNotFoundException(
                    String.format("No product purchased by user: %s on %s", username, today)
            );
        }


        List<UserPreferenceDTO> preferences = preferencesService.getUserPreferencesList(username);
        if (preferences.isEmpty()) {
            throw new DataNotFoundException("No preferences found for user: " + username);
        }

        RecommendationRequestDTO requestDTO = new RecommendationRequestDTO(
                productBarcode,
                N_RECOMMENDATION_LIMIT,
                preferences
        );

        return recommendationSystemClient
                .submitProductsForRecommendation(requestDTO)
                .doOnSuccess(response ->
                        logger.debug("Successfully retrieved recommendations for user: {}", username)
                )
                .doOnError(error ->
                        logger.error("Error getting recommendations for user: {}: {}",
                                username, error.getMessage())
                )
                .onErrorResume(WebClientRequestException.class, e ->
                        Mono.error(new ServiceUnavailableException("Recommendation service is currently unavailable"))
                )
                .onErrorResume(TimeoutException.class, e ->
                        Mono.error(new ServiceUnavailableException("Recommendation service timed out"))
                );


    }









}
