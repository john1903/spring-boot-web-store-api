package me.jangluzniewicz.webstore.security.units.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Optional;
import me.jangluzniewicz.webstore.commons.testdata.users.UserTestDataBuilder;
import me.jangluzniewicz.webstore.security.models.CustomUser;
import me.jangluzniewicz.webstore.security.services.CustomUserDetailsService;
import me.jangluzniewicz.webstore.users.interfaces.IUser;
import me.jangluzniewicz.webstore.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
  @Mock private IUser userService;
  @InjectMocks private CustomUserDetailsService customUserDetailsService;

  private User user;

  @BeforeEach
  void setUp() {
    user = UserTestDataBuilder.builder().build().buildUser();
  }

  @Test
  void loadUserByUsername_whenUserExists_thenReturnCustomUser() {
    when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

    CustomUser customUser = customUserDetailsService.loadUserByUsername(user.getEmail());

    assertEquals(user.getId(), customUser.getId());
    assertEquals(user.getEmail(), customUser.getUsername());
  }

  @Test
  void loadUserByUsername_whenUserNotExists_thenThrowUsernameNotFoundException() {
    when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class,
        () -> customUserDetailsService.loadUserByUsername(user.getEmail()));
  }
}
