package me.jangluzniewicz.webstore.apiclients.interfaces;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import me.jangluzniewicz.webstore.apiclients.models.SeoDescriptionResponse;

/** Interface for interacting with the OpenAI API. */
public interface IOpenAi {

  /**
   * Retrieves the SEO description for a product based on its ID.
   *
   * @param productId the ID of the product for which to generate the SEO description; must be a
   *     positive number.
   * @return a {@link SeoDescriptionResponse} containing the SEO description for the specified
   *     product.
   */
  SeoDescriptionResponse getProductSeoDescription(@NotNull @Min(1) Long productId);
}
