package me.jangluzniewicz.webstore.utils.testdata.roles;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.roles.models.Role;

@Builder
public class RoleTestDataBuilder {
  @Default private Long id = 2L;
  @Default private String name = "USER";

  public Role buildRole() {
    return Role.builder().id(id).name(name).build();
  }
}
