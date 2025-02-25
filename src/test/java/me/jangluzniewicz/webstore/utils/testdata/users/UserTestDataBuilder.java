package me.jangluzniewicz.webstore.utils.testdata.users;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.users.models.User;
import me.jangluzniewicz.webstore.utils.testdata.roles.RoleTestDataBuilder;

@Builder
public class UserTestDataBuilder {
  @Default private Long id = 2L;
  @Default private RoleTestDataBuilder roleBuilder = RoleTestDataBuilder.builder().build();
  @Default private String email = "client@client.com";
  @Default private String password = "$2a$10$tv8foJr7GtbQfW.kQ2E7LOJEvAVbFjhqYvxxOTaTSC8ZgTgxePcma";
  @Default private String phoneNumber = "222222222";

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
