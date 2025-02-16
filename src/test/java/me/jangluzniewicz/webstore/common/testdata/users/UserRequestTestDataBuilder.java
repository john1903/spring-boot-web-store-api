package me.jangluzniewicz.webstore.common.testdata.users;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.users.controllers.UserRequest;

@Builder
public class UserRequestTestDataBuilder {
  @Default private Long roleId = 1L;
  @Default private String email = "admin@admin.com";
  @Default private String password = "admin";
  @Default private String phoneNumber = "+48123456789";

  public UserRequest buildUserRequest() {
    return new UserRequest(roleId, email, password, phoneNumber);
  }
}
