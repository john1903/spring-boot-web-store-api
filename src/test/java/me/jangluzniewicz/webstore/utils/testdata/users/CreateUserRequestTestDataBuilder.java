package me.jangluzniewicz.webstore.utils.testdata.users;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.users.controllers.CreateUserRequest;

@Builder
public class CreateUserRequestTestDataBuilder {
  @Default private Long roleId = 2L;
  @Default private String email = "client@client.com";
  @Default private String password = "P@ssw0rd";
  @Default private String phoneNumber = "222222222";

  public CreateUserRequest buildUserRequest() {
    return new CreateUserRequest(roleId, email, password, phoneNumber);
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
