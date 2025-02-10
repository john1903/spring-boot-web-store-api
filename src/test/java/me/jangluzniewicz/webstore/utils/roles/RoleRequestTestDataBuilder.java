package me.jangluzniewicz.webstore.utils.roles;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.roles.controllers.RoleRequest;

@Builder
public class RoleRequestTestDataBuilder {
  @Default private String name = "TEST";

  public RoleRequest buildRoleRequestTestData() {
    return new RoleRequest(name);
  }
}
