package me.jangluzniewicz.webstore.apiclients.interfaces;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import me.jangluzniewicz.webstore.apiclients.models.SeoDescriptionResponse;

public interface IOpenAi {
  SeoDescriptionResponse getProductSeoDescription(@NotNull @Min(1) Long productId);
}
