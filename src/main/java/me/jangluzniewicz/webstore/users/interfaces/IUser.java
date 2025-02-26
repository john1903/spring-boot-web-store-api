package me.jangluzniewicz.webstore.users.interfaces;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;
import me.jangluzniewicz.webstore.users.controllers.UserRequest;
import me.jangluzniewicz.webstore.users.models.User;

/** Interface for managing users. */
public interface IUser {

  /**
   * Registers a new user.
   *
   * @param userRequest the request object containing the details of the user to be registered; must
   *     not be null.
   * @return an {@link IdResponse} containing the ID of the newly registered user.
   */
  IdResponse registerNewUser(@NotNull UserRequest userRequest);

  /**
   * Retrieves a user by their ID.
   *
   * @param id the ID of the user to be retrieved; must be a positive number.
   * @return an {@link Optional} containing the {@link User} if found, or empty if not found.
   */
  Optional<User> getUserById(@NotNull @Min(1) Long id);

  /**
   * Retrieves a user by their email.
   *
   * @param email the email of the user to be retrieved; must not be null.
   * @return an {@link Optional} containing the {@link User} if found, or empty if not found.
   */
  Optional<User> getUserByEmail(@NotNull String email);

  /**
   * Retrieves all users with pagination.
   *
   * @param page the page number to retrieve; must be a non-negative number.
   * @param size the number of users per page; must be a positive number.
   * @return a {@link PagedResponse} containing the paginated list of users.
   */
  PagedResponse<User> getAllUsers(@NotNull @Min(0) Integer page, @NotNull @Min(1) Integer size);

  /**
   * Updates an existing user.
   *
   * @param id the ID of the user to be updated; must be a positive number.
   * @param userRequest the request object containing the updated details of the user; must not be
   *     null.
   */
  void updateUser(@NotNull @Min(1) Long id, @NotNull UserRequest userRequest);

  /**
   * Deletes a user by their ID.
   *
   * @param id the ID of the user to be deleted; must be a positive number.
   */
  void deleteUser(@NotNull @Min(1) Long id);
}
