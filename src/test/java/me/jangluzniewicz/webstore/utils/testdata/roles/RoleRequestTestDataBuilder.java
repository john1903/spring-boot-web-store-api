package me.jangluzniewicz.webstore.utils.testdata.roles;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.roles.controllers.RoleRequest;

@Builder
public class RoleRequestTestDataBuilder {
  @Default private String name = "USER";

  public RoleRequest buildRoleRequest() {
    return new RoleRequest(name);
  }

  public String toJson() {
    return """
    {
      "name": "%s"
    }
    """
        .formatted(name);
  }
}
