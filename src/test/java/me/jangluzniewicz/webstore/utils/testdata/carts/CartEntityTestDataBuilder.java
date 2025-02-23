package me.jangluzniewicz.webstore.utils.testdata.carts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.carts.entities.CartEntity;

@Builder
public class CartEntityTestDataBuilder {
  @Default private Long id = 1L;
  @Default private Long customerId = 1L;
  @Default private BigDecimal total = BigDecimal.ZERO;

  @Default private List<CartItemEntityTestDataBuilder> items = new ArrayList<>();

  public CartEntity buildCartEntity() {
    return CartEntity.builder()
        .id(id)
        .customerId(customerId)
        .total(total)
        .items(
            items.stream()
                .map(CartItemEntityTestDataBuilder::buildCartItemEntity)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll))
        .build();
  }
}
