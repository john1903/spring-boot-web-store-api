package me.jangluzniewicz.webstore.utils.testdata.users;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.users.controllers.UserRequest;

@Builder
public class UserRequestTestDataBuilder {
  @Default private Long roleId = 2L;
  @Default private String email = "client@client.com";
  @Default private String password = "client";
  @Default private String phoneNumber = "222222222";

  public UserRequest buildUserRequest() {
    return new UserRequest(roleId, email, password, phoneNumber);
  }

  public String toJson() {
    return """
    {
      "roleId": %d,
      "email": "%s",
      "password": "%s",
      "phoneNumber": "%s"
    }
    """
        .formatted(roleId, email, password, phoneNumber);
  }
}
