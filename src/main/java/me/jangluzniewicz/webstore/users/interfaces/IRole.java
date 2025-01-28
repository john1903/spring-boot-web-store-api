package me.jangluzniewicz.webstore.users.interfaces;

import me.jangluzniewicz.webstore.users.controllers.RoleRequest;
import me.jangluzniewicz.webstore.users.models.Role;

import java.util.List;
import java.util.Optional;

public interface IRole {
    Long createNewRole(RoleRequest roleRequest);

    Optional<Role> getRoleById(Long id);

    List<Role> getAllRoles(Integer page, Integer size);

    Long updateRole(Long id, RoleRequest roleRequest);

    void deleteRole(Long id);
}
