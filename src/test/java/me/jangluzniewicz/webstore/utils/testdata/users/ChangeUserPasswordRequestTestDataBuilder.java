package me.jangluzniewicz.webstore.utils.testdata.users;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.users.controllers.ChangeUserPasswordRequest;

@Builder
public class ChangeUserPasswordRequestTestDataBuilder {
  @Default private String currentPassword = "client";
  @Default private String newPassword = "NewP@ssw0rd";

  public ChangeUserPasswordRequest buildUserRequest() {
    return new ChangeUserPasswordRequest(currentPassword, newPassword);
  }

  public String toJson() {
    return """
    {
      "currentPassword": "%s",
      "newPassword": "%s"
    }
    """
        .formatted(currentPassword, newPassword);
  }
}
