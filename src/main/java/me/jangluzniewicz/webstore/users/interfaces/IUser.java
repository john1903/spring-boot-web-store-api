package me.jangluzniewicz.webstore.users.interfaces;

import java.util.Optional;
import me.jangluzniewicz.webstore.common.models.IdResponse;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.users.controllers.UserRequest;
import me.jangluzniewicz.webstore.users.models.User;

public interface IUser {
  IdResponse registerNewUser(UserRequest userRequest);

  Optional<User> getUserById(Long id);

  Optional<User> getUserByEmail(String email);

  PagedResponse<User> getAllUsers(Integer page, Integer size);

  void updateUser(Long id, UserRequest userRequest);

  void deleteUser(Long id);
}
