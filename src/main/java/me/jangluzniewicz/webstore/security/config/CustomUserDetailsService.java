package me.jangluzniewicz.webstore.security.config;

import me.jangluzniewicz.webstore.users.services.UserService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.getUserByEmail(username)
                .map(this::mapUserToUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + username + " not found"));
    }

    private UserDetails mapUserToUserDetails(me.jangluzniewicz.webstore.users.models.User user) {
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().getName())
                .build();
    }
}
