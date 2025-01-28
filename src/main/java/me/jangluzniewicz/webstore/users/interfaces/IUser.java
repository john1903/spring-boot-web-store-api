package me.jangluzniewicz.webstore.users.interfaces;

import me.jangluzniewicz.webstore.users.controllers.UserRequest;
import me.jangluzniewicz.webstore.users.models.User;

import java.util.List;
import java.util.Optional;

public interface IUser {
    Long registerNewUser(UserRequest userRequest);

    Optional<User> getUserById(Long id);

    Optional<User> getUserByEmail(String email);

    List<User> getAllUsers(Integer page, Integer size);

    Long updateUser(Long id, UserRequest userRequest);

    void deleteUser(Long id);
}
