package com.adriangarciao.person_productivity_app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Basic OpenAPI configuration to provide API metadata for Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Person Productivity API")
                        .version("v1")
                        .description("API for managing persons and tasks")
                        .contact(new Contact().name("Adrian Garcia").email("adrian@example.com"))
                        .license(new License().name("MIT")));
    }
}
