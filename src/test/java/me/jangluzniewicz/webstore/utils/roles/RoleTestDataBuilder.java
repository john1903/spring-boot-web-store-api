package me.jangluzniewicz.webstore.utils.roles;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.roles.models.Role;

@Builder
public class RoleTestDataBuilder {
  private Long id;
  @Default private String name = "TEST";

  public Role buildRole() {
    return Role.builder().id(id).name(name).build();
  }
}
