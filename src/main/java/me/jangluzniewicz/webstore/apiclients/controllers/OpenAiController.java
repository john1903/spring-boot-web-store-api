package me.jangluzniewicz.webstore.apiclients.controllers;

import me.jangluzniewicz.webstore.apiclients.interfaces.IOpenAi;
import me.jangluzniewicz.webstore.apiclients.models.SeoDescriptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenAiController {
  private final IOpenAi openAi;

  public OpenAiController(IOpenAi openAi) {
    this.openAi = openAi;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("products/{id}/seo-description")
  public ResponseEntity<SeoDescriptionResponse> getSeoDescription(@PathVariable Long id) {
    return ResponseEntity.ok(openAi.getProductSeoDescription(id));
  }
}
