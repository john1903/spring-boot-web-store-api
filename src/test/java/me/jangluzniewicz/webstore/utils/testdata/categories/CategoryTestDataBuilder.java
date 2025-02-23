package me.jangluzniewicz.webstore.utils.testdata.categories;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.categories.models.Category;

@Builder
public class CategoryTestDataBuilder {
  @Default private Long id = 1L;
  @Default private String name = "Bicycles";

  public Category buildCategory() {
    return Category.builder().id(id).name(name).build();
  }
}
