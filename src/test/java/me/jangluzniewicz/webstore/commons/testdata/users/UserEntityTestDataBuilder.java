package me.jangluzniewicz.webstore.commons.testdata.users;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.commons.testdata.roles.RoleEntityTestDataBuilder;
import me.jangluzniewicz.webstore.users.entities.UserEntity;

@Builder
public class UserEntityTestDataBuilder {
  @Default private Long id = 1L;

  @Default
  private RoleEntityTestDataBuilder roleBuilder = RoleEntityTestDataBuilder.builder().build();

  @Default private String email = "admin@admin.com";
  @Default private String password = "$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC";
  @Default private String phoneNumber = "+48123123123";

  public UserEntity buildUserEntity() {
    return UserEntity.builder()
        .id(id)
        .role(roleBuilder.buildRoleEntity())
        .email(email)
        .password(password)
        .phoneNumber(phoneNumber)
        .build();
  }
}
