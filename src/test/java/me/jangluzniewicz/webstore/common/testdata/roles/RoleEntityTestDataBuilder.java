package me.jangluzniewicz.webstore.common.testdata.roles;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.roles.entities.RoleEntity;

@Builder
public class RoleEntityTestDataBuilder {
  private Long id;
  @Default private String name = "ADMIN";

  public RoleEntity buildRoleEntity() {
    return RoleEntity.builder().id(id).name(name).build();
  }
}
