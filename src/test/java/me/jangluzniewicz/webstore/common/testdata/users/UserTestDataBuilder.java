package me.jangluzniewicz.webstore.common.testdata.users;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.common.testdata.roles.RoleTestDataBuilder;
import me.jangluzniewicz.webstore.users.models.User;

@Builder
public class UserTestDataBuilder {
  @Default private Long id = 1L;
  @Default private RoleTestDataBuilder roleBuilder = RoleTestDataBuilder.builder().build();
  @Default private String email = "admin@admin.com";
  @Default private String password = "$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC";
  @Default private String phoneNumber = "+48123123123";

  public User buildUser() {
    return User.builder()
        .id(id)
        .role(roleBuilder.buildRole())
        .email(email)
        .password(password)
        .phoneNumber(phoneNumber)
        .build();
  }
}
