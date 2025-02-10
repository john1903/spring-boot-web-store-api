package me.jangluzniewicz.webstore.utils.carts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.carts.models.Cart;
import me.jangluzniewicz.webstore.carts.models.CartItem;

@Builder
public class CartTestDataBuilder {
  private Long id;
  private Long customerId;
  @Default private BigDecimal total = BigDecimal.ZERO;
  @Default private List<CartItem> items = new ArrayList<>();

  public Cart buildCart() {
    return Cart.builder().id(id).customerId(customerId).total(total).items(items).build();
  }
}
