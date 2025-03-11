package me.jangluzniewicz.webstore.utils.testdata.users;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.users.controllers.UpdateUserRequest;

@Builder
public class UpdateUserRequestTestDataBuilder {
  @Default private Long roleId = 2L;
  @Default private String email = "client@client.com";
  @Default private String phoneNumber = "222222222";

  public UpdateUserRequest buildUserRequest() {
    return new UpdateUserRequest(roleId, email, phoneNumber);
  }

  public String toJson() {
    return """
    {
      "roleId": %d,
      "email": "%s",
      "phoneNumber": "%s"
    }
    """
        .formatted(roleId, email, phoneNumber);
  }
}
