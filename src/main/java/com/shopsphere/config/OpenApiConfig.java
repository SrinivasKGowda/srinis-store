package com.shopsphere.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI srinisStoreOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Srini's Store API")
                        .version("1.0")
                        .description("REST API for Srini's Store e-commerce platform"));
    }
}
