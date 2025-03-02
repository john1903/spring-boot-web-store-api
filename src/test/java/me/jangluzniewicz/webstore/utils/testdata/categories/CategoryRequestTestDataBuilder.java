package me.jangluzniewicz.webstore.utils.testdata.categories;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.categories.controllers.CategoryRequest;

@Builder
public class CategoryRequestTestDataBuilder {
  @Default private String name = "Electronics";

  public CategoryRequest buildCategoryRequest() {
    return new CategoryRequest(null, name);
  }

  public String toJson() {
    return """
    {
      "name": "%s"
    }
    """
        .formatted(name);
  }
}
