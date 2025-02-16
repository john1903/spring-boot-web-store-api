package me.jangluzniewicz.webstore.common.testdata.orders;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.models.Rating;

@Builder
public class RatingTestDataBuilder {
  @Default private Long id = 1L;
  @Default private Integer rating = 5;
  @Default private String description = "Great product!";

  public Rating buildRating() {
    return Rating.builder().id(id).rating(rating).description(description).build();
  }
}
