package me.jangluzniewicz.webstore.utils.testdata.categories;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;

@Builder
public class CategoryEntityTestDataBuilder {
  @Default private Long id = 1L;
  @Default private String name = "Electronics";

  public CategoryEntity buildCategoryEntity() {
    return CategoryEntity.builder().id(id).name(name).build();
  }
}
