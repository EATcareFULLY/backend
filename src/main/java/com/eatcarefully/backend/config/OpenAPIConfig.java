package com.eatcarefully.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI shoppingOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("EATcareFULLY API")
                        .description("API for scanning and managing products")
                        .version("v1.0.0"));
    }
}