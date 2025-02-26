package me.jangluzniewicz.webstore.roles.interfaces;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;
import me.jangluzniewicz.webstore.roles.controllers.RoleRequest;
import me.jangluzniewicz.webstore.roles.models.Role;

/** Interface for managing roles. */
public interface IRole {

  /**
   * Creates a new role.
   *
   * @param roleRequest the request object containing the details of the role to be created; must
   *     not be null.
   * @return an {@link IdResponse} containing the ID of the newly created role.
   */
  IdResponse createNewRole(@NotNull RoleRequest roleRequest);

  /**
   * Retrieves a role by its ID.
   *
   * @param id the ID of the role to be retrieved; must be a positive number.
   * @return an {@link Optional} containing the {@link Role} if found, or empty if not found.
   */
  Optional<Role> getRoleById(@NotNull @Min(1) Long id);

  /**
   * Retrieves all roles with pagination.
   *
   * @param page the page number to retrieve; must be a non-negative number.
   * @param size the number of roles per page; must be a positive number.
   * @return a {@link PagedResponse} containing the paginated list of roles.
   */
  PagedResponse<Role> getAllRoles(@NotNull @Min(0) Integer page, @NotNull @Min(1) Integer size);

  /**
   * Updates an existing role.
   *
   * @param id the ID of the role to be updated; must be a positive number.
   * @param roleRequest the request object containing the updated details of the role; must not be
   *     null.
   */
  void updateRole(@NotNull @Min(1) Long id, @NotNull RoleRequest roleRequest);

  /**
   * Deletes a role by its ID.
   *
   * @param id the ID of the role to be deleted; must be a positive number.
   */
  void deleteRole(@NotNull @Min(1) Long id);
}
