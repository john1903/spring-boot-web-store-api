package me.jangluzniewicz.webstore.roles.interfaces;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;
import me.jangluzniewicz.webstore.roles.controllers.RoleRequest;
import me.jangluzniewicz.webstore.roles.models.Role;

public interface IRole {
  IdResponse createNewRole(@NotNull RoleRequest roleRequest);

  Optional<Role> getRoleById(@NotNull @Min(1) Long id);

  PagedResponse<Role> getAllRoles(@NotNull @Min(0) Integer page, @NotNull @Min(1) Integer size);

  void updateRole(@NotNull @Min(1) Long id, @NotNull RoleRequest roleRequest);

  void deleteRole(@NotNull @Min(1) Long id);
}
