package me.jangluzniewicz.webstore.categories.controllers;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CategoryRequest {
  @NotNull(message = "name is required")
  @Size(min = 1, max = 255, message = "name must be between 1 and 255 characters")
  private String name;
}
