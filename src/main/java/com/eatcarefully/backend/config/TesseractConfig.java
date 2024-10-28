package com.eatcarefully.backend.config;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class TesseractConfig {

    @Value("${tesseract.path}")
    private String tesseractPath;

    @Bean
    public ITesseract tesseract() {
        ITesseract tesseract = new Tesseract();

        if (StringUtils.hasLength(tesseractPath)) {
            tesseract.setDatapath(tesseractPath);
        }

        return tesseract;
    }
}
