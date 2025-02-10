package me.jangluzniewicz.webstore.common.units;

import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.order_statuses.entities.OrderStatusEntity;
import me.jangluzniewicz.webstore.order_statuses.models.OrderStatus;
import me.jangluzniewicz.webstore.roles.entities.RoleEntity;
import me.jangluzniewicz.webstore.roles.models.Role;
import me.jangluzniewicz.webstore.users.entities.UserEntity;
import me.jangluzniewicz.webstore.users.models.User;

public class UnitTestDataFactory {
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
}
