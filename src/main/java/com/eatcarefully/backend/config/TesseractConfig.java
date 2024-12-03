package com.eatcarefully.backend.config;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

@Configuration
@Slf4j
public class TesseractConfig {

    @Value("${tesseract.path}")
    private String tesseractPath;

    private final ResourceLoader resourceLoader;

    public TesseractConfig(ResourceLoader resourceLoader,
                           @Value("${tesseract.path}") String tesseractPath) {
        this.resourceLoader = resourceLoader;
        this.tesseractPath = tesseractPath;
    }

    @Bean
    public ITesseract tesseract() {
        ITesseract tesseract = new Tesseract();

        if (StringUtils.hasLength(tesseractPath)) {
            log.info("Setting tessdata path to: {}", tesseractPath);
            tesseract.setDatapath(tesseractPath);
        } else {
            log.warn("No tessdata path configured!");
        }

        return tesseract;
    }
}
