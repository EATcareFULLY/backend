package com.eatcarefully.backend.config;


import com.eatcarefully.backend.dto.ChatPromptConfigDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class ChatPromptConfig {


    private String labelAnalysisTask = "task";
    private String labelAnalysisResponseFormat = "format";
    private String labelAnalysisPrefix = "prefix";


    public ChatPromptConfig(){

        loadConfig();


    }

    private void loadConfig() {

        ObjectMapper mapper = new ObjectMapper();

        try{

            File file = new File("src/main/resources/prompts.json");
             ChatPromptConfigDTO dto = mapper.readValue(file, ChatPromptConfigDTO.class);
             this.labelAnalysisTask = dto.getLabelAnalysisTask();
             this.labelAnalysisResponseFormat = dto.getLabelAnalysisResponseFormat();
             this.labelAnalysisPrefix = dto.getLabelAnalysisPrefix();


        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getLabelAnalysisTask() {
        return labelAnalysisTask;
    }

    public String getLabelAnalysisResponseFormat() {
        return labelAnalysisResponseFormat;
    }

    public String getLabelAnalysisPrefix() {
        return labelAnalysisPrefix;
    }
}
