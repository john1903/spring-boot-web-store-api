package me.jangluzniewicz.webstore.categories.controllers;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "Payload for creating a new category")
@AllArgsConstructor
@Getter
public class CategoryRequest {
  @Schema(description = "Image of the category", nullable = true)
  private MultipartFile image;

  @Schema(description = "Name of the category", example = "Bicycles")
  @NotNull(message = "name is required")
  @Size(min = 1, max = 255, message = "name must be between 1 and 255 characters")
  private String name;
}
