package me.jangluzniewicz.webstore.utils.testdata.users;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.users.entities.UserEntity;
import me.jangluzniewicz.webstore.utils.testdata.roles.RoleEntityTestDataBuilder;

@Builder
public class UserEntityTestDataBuilder {
  @Default private Long id = 2L;

  @Default
  private RoleEntityTestDataBuilder roleBuilder = RoleEntityTestDataBuilder.builder().build();

  @Default private String email = "client@client.com";
  @Default private String password = "$2a$10$tv8foJr7GtbQfW.kQ2E7LOJEvAVbFjhqYvxxOTaTSC8ZgTgxePcma";
  @Default private String phoneNumber = "222222222";

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
