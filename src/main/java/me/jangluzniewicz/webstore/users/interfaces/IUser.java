package me.jangluzniewicz.webstore.users.interfaces;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import me.jangluzniewicz.webstore.common.models.IdResponse;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.users.controllers.UserRequest;
import me.jangluzniewicz.webstore.users.models.User;

public interface IUser {
  IdResponse registerNewUser(@NotNull UserRequest userRequest);

  Optional<User> getUserById(@NotNull @Min(1) Long id);

  Optional<User> getUserByEmail(@NotNull String email);

  PagedResponse<User> getAllUsers(@NotNull @Min(0) Integer page, @NotNull @Min(1) Integer size);

  void updateUser(@NotNull @Min(1) Long id, @NotNull UserRequest userRequest);

  void deleteUser(@NotNull @Min(1) Long id);
}
