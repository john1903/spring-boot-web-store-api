package me.jangluzniewicz.webstore.utils.testdata.orders;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.controllers.RatingRequest;

@Builder
public class RatingRequestTestDataBuilder {
  @Default private Long id = 1L;
  @Default private Integer rating = 5;
  @Default private String description = "Great product!";

  public RatingRequest buildRatingRequest() {
    return new RatingRequest(id, rating, description);
  }
}
