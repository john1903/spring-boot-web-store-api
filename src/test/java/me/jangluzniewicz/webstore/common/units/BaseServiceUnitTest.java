package me.jangluzniewicz.webstore.common.units;

import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.order_statuses.entities.OrderStatusEntity;
import me.jangluzniewicz.webstore.order_statuses.models.OrderStatus;
import me.jangluzniewicz.webstore.roles.entities.RoleEntity;
import me.jangluzniewicz.webstore.roles.models.Role;
import me.jangluzniewicz.webstore.users.entities.UserEntity;
import me.jangluzniewicz.webstore.users.models.User;
import org.junit.jupiter.api.BeforeEach;

public class BaseServiceUnitTest {
  protected RoleEntity roleEntity;
  protected Role role;
  protected CategoryEntity categoryEntity;
  protected Category category;
  protected OrderStatusEntity orderStatusEntity;
  protected OrderStatus orderStatus;
  protected UserEntity userEntity;
  protected User user;

  @BeforeEach
  void setUp() {
    roleEntity = UnitTestDataFactory.createRoleEntity();
    role = UnitTestDataFactory.createRole();
    categoryEntity = UnitTestDataFactory.createCategoryEntity();
    category = UnitTestDataFactory.createCategory();
    orderStatusEntity = UnitTestDataFactory.createOrderStatusEntity();
    orderStatus = UnitTestDataFactory.createOrderStatus();
    userEntity = UnitTestDataFactory.createUserEntity();
    user = UnitTestDataFactory.createUser();
  }
}
