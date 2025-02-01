package me.jangluzniewicz.webstore.roles.interfaces;

import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.roles.controllers.RoleRequest;
import me.jangluzniewicz.webstore.roles.models.Role;

import java.util.Optional;

public interface IRole {
    Long createNewRole(RoleRequest roleRequest);

    Optional<Role> getRoleById(Long id);

    PagedResponse<Role> getAllRoles(Integer page, Integer size);

    Long updateRole(Long id, RoleRequest roleRequest);

    void deleteRole(Long id);
}
