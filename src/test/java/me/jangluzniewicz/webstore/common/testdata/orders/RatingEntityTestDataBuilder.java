package me.jangluzniewicz.webstore.common.testdata.orders;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.entities.RatingEntity;

@Builder
public class RatingEntityTestDataBuilder {
  @Default private Long id = 1L;
  @Default private Integer rating = 5;
  @Default private String description = "Great product!";

  public RatingEntity buildRatingEntity() {
    return RatingEntity.builder().id(id).rating(rating).description(description).build();
  }
}
