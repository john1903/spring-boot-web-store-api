package me.jangluzniewicz.webstore.common.units;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import me.jangluzniewicz.webstore.carts.entities.CartEntity;
import me.jangluzniewicz.webstore.carts.entities.CartItemEntity;
import me.jangluzniewicz.webstore.carts.models.Cart;
import me.jangluzniewicz.webstore.carts.models.CartItem;
import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.order_statuses.entities.OrderStatusEntity;
import me.jangluzniewicz.webstore.order_statuses.models.OrderStatus;
import me.jangluzniewicz.webstore.orders.entities.OrderEntity;
import me.jangluzniewicz.webstore.orders.entities.OrderItemEntity;
import me.jangluzniewicz.webstore.orders.models.Order;
import me.jangluzniewicz.webstore.orders.models.OrderItem;
import me.jangluzniewicz.webstore.products.entities.ProductEntity;
import me.jangluzniewicz.webstore.products.models.Product;
import me.jangluzniewicz.webstore.roles.entities.RoleEntity;
import me.jangluzniewicz.webstore.roles.models.Role;
import me.jangluzniewicz.webstore.users.entities.UserEntity;
import me.jangluzniewicz.webstore.users.models.User;

public class TestDataFactory {
  public static RoleEntity createRoleEntity() {
    return RoleEntity.builder().id(1L).name("ADMIN").build();
  }

  public static Role createRole() {
    return Role.builder().id(1L).name("ADMIN").build();
  }

  public static CategoryEntity createCategoryEntity() {
    return CategoryEntity.builder().id(1L).name("Electronics").build();
  }

  public static Category createCategory() {
    return Category.builder().id(1L).name("Electronics").build();
  }

  public static OrderStatusEntity createOrderStatusEntity() {
    return OrderStatusEntity.builder().id(1L).name("PENDING").build();
  }

  public static OrderStatus createOrderStatus() {
    return OrderStatus.builder().id(1L).name("PENDING").build();
  }

  public static UserEntity createUserEntity() {
    return UserEntity.builder()
        .id(1L)
        .role(createRoleEntity())
        .email("admin@admin.com")
        .password("$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC")
        .phoneNumber("+48123123123")
        .build();
  }

  public static User createUser() {
    return User.builder()
        .id(1L)
        .role(createRole())
        .email("admin@admin.com")
        .password("$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC")
        .phoneNumber("+48123123123")
        .build();
  }

  public static ProductEntity createProductEntity() {
    return ProductEntity.builder()
        .id(1L)
        .name("Bicycle")
        .description("Mountain bike")
        .price(BigDecimal.valueOf(1000.0))
        .weight(BigDecimal.valueOf(10.0))
        .category(createCategoryEntity())
        .build();
  }

  public static Product createProduct() {
    return Product.builder()
        .id(1L)
        .name("Bicycle")
        .description("Mountain bike")
        .price(BigDecimal.valueOf(1000.0))
        .weight(BigDecimal.valueOf(10.0))
        .category(createCategory())
        .build();
  }

  public static CartItemEntity createCartItemEntity() {
    return CartItemEntity.builder().id(1L).product(createProductEntity()).quantity(1).build();
  }

  public static CartItem createCartItem() {
    return CartItem.builder().id(1L).product(createProduct()).quantity(1).build();
  }

  public static CartEntity createEmptyCartEntity() {
    return CartEntity.builder()
        .id(1L)
        .customerId(1L)
        .total(BigDecimal.ZERO)
        .items(new ArrayList<>())
        .build();
  }

  public static Cart createEmptyCart() {
    return Cart.builder()
        .id(1L)
        .customerId(1L)
        .total(BigDecimal.ZERO)
        .items(new ArrayList<>())
        .build();
  }

  public static CartEntity createCartEntity() {
    return CartEntity.builder()
        .id(1L)
        .customerId(1L)
        .total(BigDecimal.valueOf(1000.0))
        .items(List.of(createCartItemEntity()))
        .build();
  }

  public static Cart createCart() {
    return Cart.builder()
        .id(1L)
        .customerId(1L)
        .total(BigDecimal.valueOf(1000.0))
        .items(List.of(createCartItem()))
        .build();
  }

  public static OrderItemEntity createOrderItemEntity() {
    var product = createProductEntity();
    return OrderItemEntity.builder()
        .id(1L)
        .product(product)
        .price(product.getPrice())
        .discount(BigDecimal.ONE)
        .quantity(1)
        .build();
  }

  public static OrderItem createOrderItem() {
    var product = createProduct();
    return OrderItem.builder()
        .id(1L)
        .product(product)
        .price(product.getPrice())
        .discount(BigDecimal.ONE)
        .quantity(1)
        .build();
  }

  public static OrderEntity createOrderEntity() {
    return OrderEntity.builder()
        .id(1L)
        .customer(createUserEntity())
        .status(createOrderStatusEntity())
        .items(List.of(createOrderItemEntity()))
        .build();
  }

  public static Order createOrder() {
    return Order.builder()
        .id(1L)
        .customer(createUser())
        .status(createOrderStatus())
        .items(List.of(createOrderItem()))
        .build();
  }
}
