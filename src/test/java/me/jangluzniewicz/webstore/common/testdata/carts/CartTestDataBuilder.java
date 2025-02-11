package me.jangluzniewicz.webstore.common.testdata.carts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.carts.models.Cart;

@Builder
public class CartTestDataBuilder {
  private Long id;
  private Long customerId;
  @Default private BigDecimal total = BigDecimal.ZERO;
  @Default private List<CartItemTestDataBuilder> items = new ArrayList<>();

  public Cart buildCart() {
    return Cart.builder()
        .id(id)
        .customerId(customerId)
        .total(total)
        .items(
            items.stream()
                .map(CartItemTestDataBuilder::buildCartItem)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll))
        .build();
  }
}
