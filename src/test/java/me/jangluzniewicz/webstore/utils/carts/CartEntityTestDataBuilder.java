package me.jangluzniewicz.webstore.utils.carts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.carts.entities.CartEntity;
import me.jangluzniewicz.webstore.carts.entities.CartItemEntity;

@Builder
public class CartEntityTestDataBuilder {
  private Long id;
  private Long customerId;
  @Default private BigDecimal total = BigDecimal.ZERO;
  @Default private List<CartItemEntity> items = new ArrayList<>();

  public CartEntity buildCartEntity() {
    return CartEntity.builder().id(id).customerId(customerId).total(total).items(items).build();
  }
}
