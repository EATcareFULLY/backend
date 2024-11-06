package com.eatcarefully.backend.service;

import java.io.IOException;

public interface IChatLabelAnalysisService {



    public String createPrompt(String textContent);

    public String getChatResponse(String prompt) throws IOException, InterruptedException;




}
