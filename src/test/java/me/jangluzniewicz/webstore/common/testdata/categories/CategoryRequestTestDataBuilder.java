package me.jangluzniewicz.webstore.common.testdata.categories;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.categories.controllers.CategoryRequest;

@Builder
public class CategoryRequestTestDataBuilder {
  @Default private String name = "Bicycles";

  public CategoryRequest buildCategoryRequest() {
    return new CategoryRequest(name);
  }
}
