package com.eatcarefully.backend.config;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Configuration
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
            try {
                Resource resource = resourceLoader.getResource(tesseractPath);
                String absolutePath = resource.getFile().getAbsolutePath();
                tesseract.setDatapath(absolutePath);
            } catch (IOException e) {
                throw new BeanCreationException("Failed to initialize Tesseract with tessdata located in resources", e);
            }
        }

        return tesseract;
    }
}
