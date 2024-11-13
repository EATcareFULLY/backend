package com.eatcarefully.backend.controller;


import com.eatcarefully.backend.service.OCRService;
import org.json.JSONObject;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;


@RestController
@RequestMapping("/services")
@AllArgsConstructor
public class DispatcherController {


    private final WebClient webClient = WebClient.create();
    private OCRService ocrService;


    @PostMapping("/label-analysis")
    public Mono<Map> handleLabelAnalysisRequests(@RequestParam MultipartFile file) {
        String url = "http://localhost/label-analysis/";

        String labelText = ocrService.extractTextFromFile(file).getBody();
        JSONObject json = new JSONObject();
        json.put("label_text", labelText);

        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json.toMap())
                .retrieve()
                .bodyToMono(Map.class);
    }





}
