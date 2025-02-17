package me.jangluzniewicz.webstore.apiclients.interfaces;

import me.jangluzniewicz.webstore.apiclients.models.SeoDescriptionResponse;

public interface IOpenAi {
  SeoDescriptionResponse getProductSeoDescription(Long productId);
}
