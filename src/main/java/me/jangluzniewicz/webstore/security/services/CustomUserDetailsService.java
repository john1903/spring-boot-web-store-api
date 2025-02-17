package me.jangluzniewicz.webstore.security.services;

import java.util.List;
import me.jangluzniewicz.webstore.security.models.CustomUser;
import me.jangluzniewicz.webstore.users.services.UserService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UserService userService;

  public CustomUserDetailsService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public CustomUser loadUserByUsername(String username) throws UsernameNotFoundException {
    return userService
        .getUserByEmail(username)
        .map(this::mapUserToUserDetails)
        .orElseThrow(
            () -> new UsernameNotFoundException("User with email " + username + " not found"));
  }

  private CustomUser mapUserToUserDetails(me.jangluzniewicz.webstore.users.models.User user) {
    return CustomUser.customBuilder()
        .id(user.getId())
        .username(user.getEmail())
        .password(user.getPassword())
        .roles(List.of(user.getRole().getName()))
        .build();
  }
}
