package me.jangluzniewicz.webstore.common.testdata.roles;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.roles.entities.RoleEntity;

@Builder
public class RoleEntityTestDataBuilder {
  @Default private Long id = 1L;
  @Default private String name = "EMPLOYEE";

  public RoleEntity buildRoleEntity() {
    return RoleEntity.builder().id(id).name(name).build();
  }
}
