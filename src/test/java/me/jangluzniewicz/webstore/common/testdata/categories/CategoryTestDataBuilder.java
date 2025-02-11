package me.jangluzniewicz.webstore.common.testdata.categories;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.categories.models.Category;

@Builder
public class CategoryTestDataBuilder {
  private Long id;
  @Default private String name = "Bicycles";

  public Category buildCategory() {
    return Category.builder().id(id).name(name).build();
  }
}
