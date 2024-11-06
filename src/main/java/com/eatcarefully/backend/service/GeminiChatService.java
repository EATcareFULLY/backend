package com.eatcarefully.backend.service;

import com.eatcarefully.backend.config.ChatPromptConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class GeminiChatService implements IChatLabelAnalysisService{



    private ChatPromptConfig promptConfig;



    @Override
    public String createPrompt(String textContent) {


        return String.format("%s %s %s %s",
                promptConfig.getLabelAnalysisTask(),
                promptConfig.getLabelAnalysisResponseFormat(),
                promptConfig.getLabelAnalysisPrefix(),
                textContent);
    }

    @Override
    public String getChatResponse(String prompt) throws IOException, InterruptedException {
        return null;
    }
}
