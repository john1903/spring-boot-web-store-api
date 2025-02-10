package me.jangluzniewicz.webstore.common.testdata.roles;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.roles.controllers.RoleRequest;

@Builder
public class RoleRequestTestDataBuilder {
  @Default private String name = "ADMIN";

  public RoleRequest buildRoleRequest() {
    return new RoleRequest(name);
  }
}
