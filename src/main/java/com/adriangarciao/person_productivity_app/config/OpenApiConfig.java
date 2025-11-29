package com.adriangarciao.person_productivity_app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Basic OpenAPI configuration to provide API metadata for Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Components components = new Components();

        // Define PageResponse<TaskDto> schema
        Schema<?> pageResponseTask = new Schema<>()
            .type("object")
            .addProperties("content", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/TaskDto")))
            .addProperties("page", new Schema<>().type("integer").format("int32"))
            .addProperties("size", new Schema<>().type("integer").format("int32"))
            .addProperties("totalElements", new Schema<>().type("integer").format("int64"))
            .addProperties("totalPages", new Schema<>().type("integer").format("int32"));

        // Define PageResponse<PersonDto> schema
        Schema<?> pageResponsePerson = new Schema<>()
            .type("object")
            .addProperties("content", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/PersonDto")))
            .addProperties("page", new Schema<>().type("integer").format("int32"))
            .addProperties("size", new Schema<>().type("integer").format("int32"))
            .addProperties("totalElements", new Schema<>().type("integer").format("int64"))
            .addProperties("totalPages", new Schema<>().type("integer").format("int32"));

        components.addSchemas("PageResponseTaskDto", pageResponseTask);
        components.addSchemas("PageResponsePersonDto", pageResponsePerson);

        return new OpenAPI()
            .components(components)
            .info(new Info()
                .title("Person Productivity API")
                .version("v1")
                .description("API for managing persons and tasks")
                .contact(new Contact().name("Adrian Garcia").email("adrian@example.com"))
                .license(new License().name("MIT")));
    }
}
