package me.jangluzniewicz.webstore.swagger.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "WebStore API", version = "v1.0.0"))
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer")
public class SwaggerConfig {
  @Bean
  public OpenAPI customOpenAPI() {
    OpenAPI openAPI = new OpenAPI();
    openAPI.path(
        "/auth/login",
        new PathItem()
            .post(
                new Operation()
                    .tags(Collections.singletonList("Authentication"))
                    .summary("User Login")
                    .description("Authenticates user and returns jwt")
                    .requestBody(
                        new RequestBody()
                            .content(
                                new Content()
                                    .addMediaType(
                                        org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                        new MediaType()
                                            .schema(
                                                new ObjectSchema()
                                                    .addProperty(
                                                        "username",
                                                        new StringSchema()
                                                            .example("admin@admin.com"))
                                                    .addProperty(
                                                        "password",
                                                        new StringSchema().example("admin"))))))
                    .responses(
                        new ApiResponses()
                            .addApiResponse(
                                "200",
                                new ApiResponse()
                                    .description("User authenticated successfully")
                                    .content(
                                        new Content()
                                            .addMediaType(
                                                org.springframework.http.MediaType
                                                    .APPLICATION_JSON_VALUE,
                                                new MediaType()
                                                    .schema(
                                                        new ObjectSchema()
                                                            .addProperty(
                                                                "token",
                                                                new StringSchema()
                                                                    .example(
                                                                        "eyJhbGciOiJIUzI1NiJ9.eyJzdWIi..."))))))
                            .addApiResponse(
                                "401", new ApiResponse().description("Unauthorized")))));

    return openAPI;
  }
}
