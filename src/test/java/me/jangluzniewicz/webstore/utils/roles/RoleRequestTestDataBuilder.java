package me.jangluzniewicz.webstore.utils.roles;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.roles.controllers.RoleRequest;

@Builder
public class RoleRequestTestDataBuilder {
  @Default private String name = "PENDING";

  public RoleRequest buildRoleRequest() {
    return new RoleRequest(name);
  }
}
