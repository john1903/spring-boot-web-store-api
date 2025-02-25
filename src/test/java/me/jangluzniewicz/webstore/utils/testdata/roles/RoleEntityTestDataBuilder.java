package me.jangluzniewicz.webstore.utils.testdata.roles;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.roles.entities.RoleEntity;

@Builder
public class RoleEntityTestDataBuilder {
  @Default private Long id = 2L;
  @Default private String name = "CUSTOMER";

  public RoleEntity buildRoleEntity() {
    return RoleEntity.builder().id(id).name(name).build();
  }
}
