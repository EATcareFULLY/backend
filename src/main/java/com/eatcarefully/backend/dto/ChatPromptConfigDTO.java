package com.eatcarefully.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
public class ChatPromptConfigDTO {

    private String labelAnalysisTask;
    private String labelAnalysisResponseFormat;
    private String labelAnalysisPrefix;

}
