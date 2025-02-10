package me.jangluzniewicz.webstore.users.interfaces;

import java.util.Optional;
import me.jangluzniewicz.webstore.users.controllers.UserRequest;
import me.jangluzniewicz.webstore.users.models.User;
import me.jangluzniewicz.webstore.utils.models.PagedResponse;

public interface IUser {
  Long registerNewUser(UserRequest userRequest);

  Optional<User> getUserById(Long id);

  Optional<User> getUserByEmail(String email);

  PagedResponse<User> getAllUsers(Integer page, Integer size);

  Long updateUser(Long id, UserRequest userRequest);

  void deleteUser(Long id);
}
