package me.jangluzniewicz.webstore.apiclients.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Response model containing SEO description for a product")
@AllArgsConstructor
@Getter
public class SeoDescriptionResponse {
  @Schema(
      description = "SEO optimized description",
      example = "High quality, affordable product with excellent performance and durability.")
  private String description;
}
