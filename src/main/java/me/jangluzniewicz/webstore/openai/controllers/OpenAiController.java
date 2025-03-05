package me.jangluzniewicz.webstore.openai.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.jangluzniewicz.webstore.openai.interfaces.IOpenAi;
import me.jangluzniewicz.webstore.openai.models.SeoDescriptionResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OpenAi", description = "Operations related to OpenAI integration")
@RestController
public class OpenAiController {
  private final IOpenAi openAi;

  public OpenAiController(IOpenAi openAi) {
    this.openAi = openAi;
  }

  @Operation(
      summary = "Generate SEO description",
      description = "Generates a SEO description for the product with the provided ID",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(
      responseCode = "200",
      description = "SEO description generated successfully",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = SeoDescriptionResponse.class)))
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("products/{id}/seo-description")
  public ResponseEntity<SeoDescriptionResponse> getSeoDescription(
      @Parameter(
              in = ParameterIn.PATH,
              description = "ID of the product",
              required = true,
              example = "1")
          @PathVariable
          Long id) {
    return ResponseEntity.ok(openAi.getProductSeoDescription(id));
  }
}
