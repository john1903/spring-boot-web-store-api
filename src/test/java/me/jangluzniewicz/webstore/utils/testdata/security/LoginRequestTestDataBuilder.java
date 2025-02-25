package me.jangluzniewicz.webstore.utils.testdata.security;

import lombok.Builder;
import lombok.Builder.Default;

@Builder
public class LoginRequestTestDataBuilder {
  @Default private String username = "client@client.com";
  @Default private String password = "client";

  public String toJson() {
    return """
        {
          "username": "%s",
          "password": "%s"
        }
        """
        .formatted(username, password);
  }
}
