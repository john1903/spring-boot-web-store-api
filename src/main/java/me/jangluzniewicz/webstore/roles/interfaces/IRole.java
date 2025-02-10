package me.jangluzniewicz.webstore.roles.interfaces;

import java.util.Optional;
import me.jangluzniewicz.webstore.roles.controllers.RoleRequest;
import me.jangluzniewicz.webstore.roles.models.Role;
import me.jangluzniewicz.webstore.utils.models.PagedResponse;

public interface IRole {
  Long createNewRole(RoleRequest roleRequest);

  Optional<Role> getRoleById(Long id);

  PagedResponse<Role> getAllRoles(Integer page, Integer size);

  Long updateRole(Long id, RoleRequest roleRequest);

  void deleteRole(Long id);
}
