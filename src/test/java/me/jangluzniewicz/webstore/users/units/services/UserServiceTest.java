package me.jangluzniewicz.webstore.users.units.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.carts.interfaces.ICart;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.roles.entities.RoleEntity;
import me.jangluzniewicz.webstore.roles.interfaces.IRole;
import me.jangluzniewicz.webstore.roles.models.Role;
import me.jangluzniewicz.webstore.users.controllers.UserRequest;
import me.jangluzniewicz.webstore.users.entities.UserEntity;
import me.jangluzniewicz.webstore.users.mappers.UserMapper;
import me.jangluzniewicz.webstore.users.models.User;
import me.jangluzniewicz.webstore.users.repositories.UserRepository;
import me.jangluzniewicz.webstore.users.services.UserService;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock private UserRepository userRepository;
  @Mock private IRole roleService;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private UserMapper userMapper;
  @Mock private ICart cartService;
  @InjectMocks private UserService userService;

  private RoleEntity roleEntity;
  private Role role;

  @BeforeEach
  public void setUp() {
    roleEntity = RoleEntity.builder().id(1L).name("ADMIN").build();
    role = Role.builder().id(1L).name("ADMIN").build();
  }

  @Test
  public void registerNewUser_whenEmailIsUniqueAndRoleExists_thenReturnUserId() {
    when(userRepository.existsByEmailIgnoreCase("admin@admin.com")).thenReturn(false);
    when(passwordEncoder.encode("admin"))
        .thenReturn("$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC");
    when(roleService.getRoleById(any())).thenReturn(Optional.of(role));
    when(userRepository.save(any()))
        .thenReturn(
            UserEntity.builder()
                .id(1L)
                .role(roleEntity)
                .email("admin@admin.com")
                .password("$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC")
                .phoneNumber("+48123123123")
                .build());
    when(cartService.createNewCart(1L)).thenReturn(1L);

    assertEquals(
        1L,
        userService.registerNewUser(
            new UserRequest(1L, "admin@admin.com", "admin", "+48123123123")));
  }

  @Test
  public void registerNewUser_whenEmailAlreadyExists_thenThrowNotUniqueException() {
    when(userRepository.existsByEmailIgnoreCase("admin@admin.com")).thenReturn(true);

    assertThrows(
        NotUniqueException.class,
        () ->
            userService.registerNewUser(
                new UserRequest(1L, "admin@admin.com", "admin", "+48123123123")));
  }

  @Test
  public void registerNewUser_whenRoleDoesNotExist_thenThrowNotFoundException() {
    when(userRepository.existsByEmailIgnoreCase("admin@admin.com")).thenReturn(false);
    when(passwordEncoder.encode("admin"))
        .thenReturn("$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC");
    when(roleService.getRoleById(any())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () ->
            userService.registerNewUser(
                new UserRequest(1L, "admin@admin.com", "admin", "+48123123123")));
  }

  @Test
  public void updateUser_whenUserExistsAndEmailIsUnique_thenReturnUserId() {
    when(userRepository.findById(1L))
        .thenReturn(
            Optional.of(
                UserEntity.builder()
                    .id(1L)
                    .role(roleEntity)
                    .email("admin@admin.com")
                    .password("$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC")
                    .phoneNumber("+48123123123")
                    .build()));
    when(userMapper.fromEntity(any()))
        .thenReturn(
            new User(
                1L,
                role,
                "admin@admin.com",
                "$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC",
                "+48123123123"));
    when(userRepository.existsByEmailIgnoreCase("new@new.com")).thenReturn(false);
    when(roleService.getRoleById(any())).thenReturn(Optional.of(role));
    when(passwordEncoder.encode("admin"))
        .thenReturn("$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC");
    when(userRepository.save(any()))
        .thenReturn(
            UserEntity.builder()
                .id(1L)
                .role(roleEntity)
                .email("new@new.com")
                .password("$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC")
                .phoneNumber("+48123123123")
                .build());

    assertEquals(
        1L,
        userService.updateUser(1L, new UserRequest(1L, "new@new.com", "admin", "+48123123123")));
  }

  @Test
  public void updateUser_whenUserDoesNotExist_thenThrowNotFoundException() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () ->
            userService.updateUser(
                1L, new UserRequest(1L, "new@new.com", "admin", "+48123123123")));
  }

  @Test
  public void updateUser_whenEmailAlreadyExists_thenThrowNotUniqueException() {
    when(userRepository.findById(1L))
        .thenReturn(
            Optional.of(
                UserEntity.builder()
                    .id(1L)
                    .role(roleEntity)
                    .email("admin@admin.com")
                    .password("$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC")
                    .phoneNumber("+48123123123")
                    .build()));
    when(userMapper.fromEntity(any()))
        .thenReturn(
            new User(
                1L,
                role,
                "admin@admin.com",
                "$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC",
                "+48123123123"));
    when(userRepository.existsByEmailIgnoreCase("new@new.com")).thenReturn(true);

    assertThrows(
        NotUniqueException.class,
        () ->
            userService.updateUser(
                1L, new UserRequest(1L, "new@new.com", "admin", "+48123123123")));
  }

  @Test
  public void updateUser_whenEmailIsTheSame_thenDoNotThrowException() {
    when(userRepository.findById(1L))
        .thenReturn(
            Optional.of(
                UserEntity.builder()
                    .id(1L)
                    .role(roleEntity)
                    .email("admin@admin.com")
                    .password("$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC")
                    .phoneNumber("+48123123123")
                    .build()));
    when(userMapper.fromEntity(any()))
        .thenReturn(
            new User(
                1L,
                role,
                "admin@admin.com",
                "$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC",
                "+48123123123"));
    when(userRepository.existsByEmailIgnoreCase("admin@admin.com")).thenReturn(true);
    when(roleService.getRoleById(any())).thenReturn(Optional.of(role));
    when(passwordEncoder.encode("admin"))
        .thenReturn("$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC");
    when(userRepository.save(any()))
        .thenReturn(
            UserEntity.builder()
                .id(1L)
                .role(roleEntity)
                .email("admin@admin.com")
                .password("$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC")
                .phoneNumber("+48123123123")
                .build());

    assertEquals(
        1L,
        userService.updateUser(
            1L, new UserRequest(1L, "admin@admin.com", "admin", "+48123123123")));
  }

  @Test
  public void updateUser_whenRoleDoesNotExist_thenThrowNotFoundException() {
    when(userRepository.findById(1L))
        .thenReturn(
            Optional.of(
                UserEntity.builder()
                    .id(1L)
                    .role(roleEntity)
                    .email("admin@admin.com")
                    .password("$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC")
                    .phoneNumber("+48123123123")
                    .build()));
    when(userMapper.fromEntity(any()))
        .thenReturn(
            new User(
                1L,
                role,
                "admin@admin.com",
                "$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC",
                "+48123123123"));
    when(userRepository.existsByEmailIgnoreCase("admin@admin.com")).thenReturn(false);
    when(roleService.getRoleById(any())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () ->
            userService.updateUser(
                1L, new UserRequest(1L, "admin@admin.com", "admin", "+48123123123")));
  }

  @Test
  public void getUserById_whenUserExists_thenReturnUser() {
    when(userRepository.findById(1L))
        .thenReturn(
            Optional.of(
                UserEntity.builder()
                    .id(1L)
                    .role(roleEntity)
                    .email("admin@admin.com")
                    .password("$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC")
                    .phoneNumber("+48123123123")
                    .build()));
    when(userMapper.fromEntity(any()))
        .thenReturn(
            new User(
                1L,
                role,
                "admin@admin.com",
                "$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC",
                "+48123123123"));

    assertTrue(userService.getUserById(1L).isPresent());
  }

  @Test
  public void getUserById_whenUserDoesNotExist_thenReturnEmpty() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertTrue(userService.getUserById(1L).isEmpty());
  }

  @Test
  public void getUserByEmail_whenUserExists_thenReturnUser() {
    when(userRepository.findByEmailIgnoreCase("admin@admin.com"))
        .thenReturn(
            Optional.of(
                UserEntity.builder()
                    .id(1L)
                    .role(roleEntity)
                    .email("admin@admin.com")
                    .password("$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC")
                    .phoneNumber("+48123123123")
                    .build()));
    when(userMapper.fromEntity(any()))
        .thenReturn(
            new User(
                1L,
                role,
                "admin@admin.com",
                "$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC",
                "+48123123123"));

    assertTrue(userService.getUserByEmail("admin@admin.com").isPresent());
  }

  @Test
  public void getUserByEmail_whenUserDoesNotExist_thenReturnEmpty() {
    when(userRepository.findByEmailIgnoreCase("admin@admin.com")).thenReturn(Optional.empty());

    assertTrue(userService.getUserByEmail("admin@admin.com").isEmpty());
  }

  @Test
  public void getAllUsers_whenUsersExist_thenReturnPagedResponse() {
    when(userRepository.findAll(any(Pageable.class)))
        .thenReturn(
            new PageImpl<>(
                List.of(
                    UserEntity.builder()
                        .id(1L)
                        .role(roleEntity)
                        .email("admin@admin.com")
                        .password("$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC")
                        .phoneNumber("+48123123123")
                        .build())));
    when(userMapper.fromEntity(any()))
        .thenReturn(
            new User(
                1L,
                role,
                "admin@admin.com",
                "$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC",
                "+48123123123"));

    assertEquals(1, userService.getAllUsers(0, 10).getTotalPages());
  }

  @Test
  public void deleteUser_whenUserExists_thenDeleteSuccessfully() {
    when(userRepository.existsById(1L)).thenReturn(true);

    assertDoesNotThrow(() -> userService.deleteUser(1L));
  }

  @Test
  public void deleteUser_whenUserDoesNotExist_thenThrowNotFoundException() {
    when(userRepository.existsById(1L)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> userService.deleteUser(1L));
  }

  @Test
  public void deleteUser_whenUserHasDependencies_thenThrowDeletionNotAllowedException() {
    when(userRepository.existsById(1L)).thenReturn(true);
    doThrow(
            new DataIntegrityViolationException(
                "", new ConstraintViolationException("", new SQLException(), "")))
        .when(userRepository)
        .deleteById(1L);

    assertThrows(DeletionNotAllowedException.class, () -> userService.deleteUser(1L));
  }

  @Test
  public void deleteUser_whenDataIntegrityViolationIsNotConstraintRelated_thenThrowException() {
    when(userRepository.existsById(1L)).thenReturn(true);
    doThrow(new DataIntegrityViolationException("", new SQLException()))
        .when(userRepository)
        .deleteById(1L);

    assertThrows(DataIntegrityViolationException.class, () -> userService.deleteUser(1L));
  }
}
