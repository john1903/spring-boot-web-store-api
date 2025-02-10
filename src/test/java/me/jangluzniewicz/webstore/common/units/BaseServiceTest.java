package me.jangluzniewicz.webstore.common.units;

import me.jangluzniewicz.webstore.carts.entities.CartEntity;
import me.jangluzniewicz.webstore.carts.entities.CartItemEntity;
import me.jangluzniewicz.webstore.carts.models.Cart;
import me.jangluzniewicz.webstore.carts.models.CartItem;
import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.order_statuses.entities.OrderStatusEntity;
import me.jangluzniewicz.webstore.order_statuses.models.OrderStatus;
import me.jangluzniewicz.webstore.orders.entities.OrderEntity;
import me.jangluzniewicz.webstore.orders.models.Order;
import me.jangluzniewicz.webstore.products.entities.ProductEntity;
import me.jangluzniewicz.webstore.products.models.Product;
import me.jangluzniewicz.webstore.roles.entities.RoleEntity;
import me.jangluzniewicz.webstore.roles.models.Role;
import me.jangluzniewicz.webstore.users.entities.UserEntity;
import me.jangluzniewicz.webstore.users.models.User;
import org.junit.jupiter.api.BeforeEach;

public class BaseServiceTest {
  protected RoleEntity roleEntity;
  protected Role role;
  protected CategoryEntity categoryEntity;
  protected Category category;
  protected OrderStatusEntity orderStatusEntity;
  protected OrderStatus orderStatus;
  protected UserEntity userEntity;
  protected User user;
  protected ProductEntity productEntity;
  protected Product product;
  protected CartItemEntity cartItemEntity;
  protected CartItem cartItem;
  protected CartEntity cartEmptyEntity;
  protected Cart cartEmpty;
  protected CartEntity cartEntity;
  protected Cart cart;
  protected OrderEntity orderEntity;
  protected Order order;

  @BeforeEach
  void setUp() {
    categoryEntity = TestDataFactory.createCategoryEntity();
    category = TestDataFactory.createCategory();
    orderStatusEntity = TestDataFactory.createOrderStatusEntity();
    orderStatus = TestDataFactory.createOrderStatus();
    roleEntity = TestDataFactory.createRoleEntity();
    role = TestDataFactory.createRole();
    userEntity = TestDataFactory.createUserEntity();
    user = TestDataFactory.createUser();
    productEntity = TestDataFactory.createProductEntity();
    product = TestDataFactory.createProduct();
    cartItemEntity = TestDataFactory.createCartItemEntity();
    cartItem = TestDataFactory.createCartItem();
    cartEmptyEntity = TestDataFactory.createEmptyCartEntity();
    cartEmpty = TestDataFactory.createEmptyCart();
    cartEntity = TestDataFactory.createCartEntity();
    cart = TestDataFactory.createCart();
    orderEntity = TestDataFactory.createOrderEntity();
    order = TestDataFactory.createOrder();
  }
}
