package me.jangluzniewicz.webstore.common.testdata.categories;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;

@Builder
public class CategoryEntityTestDataBuilder {
  private Long id;
  @Default private String name = "Bicycles";

  public CategoryEntity buildCategoryEntity() {
    return CategoryEntity.builder().id(id).name(name).build();
  }
}
