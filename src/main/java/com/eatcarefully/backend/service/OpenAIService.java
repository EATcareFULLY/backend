package com.eatcarefully.backend.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String openAIKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;


    public String createPrompt(String text){
        return text;
    }



    public String getOpenAIResponse(String prompt) throws IOException, InterruptedException {

        try {
            HttpClient client = HttpClient.newHttpClient();

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", model);
            jsonBody.put("prompt", prompt);
            jsonBody.put("max_tokens", 10);
            jsonBody.put("temperature", 0.7);


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Bearer " + openAIKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                    .build();


            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                return jsonResponse.getJSONArray("choices").getJSONObject(0).getString("text").trim();
            } else {
                return "Error: " + response.statusCode() + " - " + response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();


        }
    }


}
