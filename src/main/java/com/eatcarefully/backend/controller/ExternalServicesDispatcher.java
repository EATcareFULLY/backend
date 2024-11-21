package com.eatcarefully.backend.controller;


import com.eatcarefully.backend.dto.HistoryAnalysisProductDTO;
import com.eatcarefully.backend.dto.LabelTextForAnalysisDTO;
import com.eatcarefully.backend.exceptions.DataNotFoundException;
import com.eatcarefully.backend.exceptions.ModelValidationException;
import com.eatcarefully.backend.exceptions.ServiceUnavailableException;
import com.eatcarefully.backend.helper.ImageHelper;
import com.eatcarefully.backend.service.OCRService;
import com.eatcarefully.backend.service.PurchaseService;
import com.eatcarefully.backend.service.external.IHistoryAnalysisClient;
import com.eatcarefully.backend.service.external.ILabelAnalysisClient;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.handler.timeout.TimeoutException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.net.http.HttpTimeoutException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/services")
@AllArgsConstructor
public class ExternalServicesDispatcher {

    private ILabelAnalysisClient labelAnalysisClient;
    private ImageHelper imageHelper;
    private OCRService ocrService;

    private PurchaseService purchaseService;

    private IHistoryAnalysisClient historyAnalysisClient;


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



    @GetMapping("/history-analysis")
    public Mono<JsonNode> handleHistoryAnalysisRequest(@AuthenticationPrincipal Jwt jwt){

        List<HistoryAnalysisProductDTO> historyProducts = purchaseService.getDataForHistoryAnalysis(jwt);

        if(historyProducts.isEmpty())
            return Mono.error(new DataNotFoundException("No data for history analysis"));

        return historyAnalysisClient.submitProductsForHistoryAnalysis(historyProducts);

    }







}
