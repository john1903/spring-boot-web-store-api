package me.jangluzniewicz.webstore.roles.interfaces;

import java.util.Optional;
import me.jangluzniewicz.webstore.common.models.IdResponse;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.roles.controllers.RoleRequest;
import me.jangluzniewicz.webstore.roles.models.Role;

public interface IRole {
  IdResponse createNewRole(RoleRequest roleRequest);

  Optional<Role> getRoleById(Long id);

  PagedResponse<Role> getAllRoles(Integer page, Integer size);

  void updateRole(Long id, RoleRequest roleRequest);

  void deleteRole(Long id);
}
