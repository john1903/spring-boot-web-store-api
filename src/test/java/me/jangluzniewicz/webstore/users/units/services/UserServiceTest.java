package me.jangluzniewicz.webstore.users.units.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.carts.interfaces.ICart;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
  private String passwordEncoded;
  private String passwordDecoded;
  private Role role;
  private RoleEntity roleEntity;

  @BeforeEach
  void setUp() {
    passwordDecoded = "admin";
    passwordEncoded = "$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC";
    role = new Role(1L, "ADMIN");
    roleEntity = new RoleEntity(1L, "ADMIN");
  }

  @Test
  public void shouldRegisterNewUserAndReturnUserId() {
    UserRequest userRequest =
        new UserRequest(1L, "admin@admin.com", passwordDecoded, "+48123456789");
    UserEntity savedEntity =
        UserEntity.builder()
            .id(1L)
            .role(roleEntity)
            .email(userRequest.getEmail())
            .password(passwordEncoded)
            .phoneNumber(userRequest.getPhoneNumber())
            .build();

    when(userRepository.existsByEmailIgnoreCase(userRequest.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(userRequest.getPassword())).thenReturn(passwordEncoded);
    when(roleService.getRoleById(userRequest.getRoleId())).thenReturn(Optional.of(role));
    when(userMapper.toEntity(any()))
        .thenReturn(
            UserEntity.builder()
                .role(roleEntity)
                .email(userRequest.getEmail())
                .password(passwordEncoded)
                .phoneNumber(userRequest.getPhoneNumber())
                .build());
    when(userRepository.save(any())).thenReturn(savedEntity);
    when(cartService.createNewCart(1L)).thenReturn(1L);

    Long userId = userService.registerNewUser(userRequest);

    assertEquals(1L, userId);
  }

  @Test
  public void shouldThrowNotUniqueExceptionWhenEmailAlreadyExists() {
    UserRequest userRequest =
        new UserRequest(1L, "admin@admin.com", passwordDecoded, "+48123456789");

    when(userRepository.existsByEmailIgnoreCase(userRequest.getEmail())).thenReturn(true);

    assertThrows(NotUniqueException.class, () -> userService.registerNewUser(userRequest));
  }

  @Test
  public void shouldThrowNotFoundExceptionWhenRoleNotFoundOnRegister() {
    UserRequest userRequest =
        new UserRequest(1L, "admin@admin.com", passwordDecoded, "+48123456789");

    when(userRepository.existsByEmailIgnoreCase(userRequest.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(userRequest.getPassword())).thenReturn(passwordEncoded);
    when(roleService.getRoleById(userRequest.getRoleId())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.registerNewUser(userRequest));
  }

  @Test
  public void shouldUpdateUserAndReturnUserId() {
    UserRequest userRequest =
        new UserRequest(1L, "admin@admin.com", passwordDecoded, "+48123123123");
    UserEntity savedEntity =
        UserEntity.builder()
            .id(1L)
            .role(roleEntity)
            .email("oldadmin@oldadmin.com")
            .password(passwordEncoded)
            .phoneNumber(userRequest.getPhoneNumber())
            .build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(savedEntity));
    when(userRepository.existsByEmailIgnoreCase(userRequest.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(userRequest.getPassword())).thenReturn(passwordEncoded);

    Long userId = userService.updateUser(1L, userRequest);

    assertEquals(1L, userId);
  }

  @Test
  public void shouldThrowNotFoundExceptionWhenUserNotFoundOnUpdate() {
    UserRequest userRequest =
        new UserRequest(1L, "admin@admin.com", passwordDecoded, "+48123456789");

    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.updateUser(1L, userRequest));
  }

  @Test
  public void shouldThrowNotUniqueExceptionWhenEmailAlreadyExistsOnUpdate() {
    UserRequest userRequest =
        new UserRequest(1L, "admin@admin.com", passwordDecoded, "+48123123123");
    UserEntity savedEntity =
        UserEntity.builder()
            .id(1L)
            .role(roleEntity)
            .email("admin2@admin.com")
            .password(passwordEncoded)
            .phoneNumber(userRequest.getPhoneNumber())
            .build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(savedEntity));
    when(userRepository.existsByEmailIgnoreCase(userRequest.getEmail())).thenReturn(true);

    assertThrows(NotUniqueException.class, () -> userService.updateUser(1L, userRequest));
  }

  @Test
  public void shouldNotThrowNotUniqueExceptionWhenEmailIsTheSameOnUpdate() {
    UserRequest userRequest =
        new UserRequest(1L, "admin@admin.com", passwordDecoded, "+48123123123");
    UserEntity savedEntity =
        UserEntity.builder()
            .id(1L)
            .role(roleEntity)
            .email(userRequest.getEmail())
            .password(passwordEncoded)
            .phoneNumber(userRequest.getPhoneNumber())
            .build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(savedEntity));
    when(userRepository.existsByEmailIgnoreCase(userRequest.getEmail())).thenReturn(true);
    when(passwordEncoder.encode(userRequest.getPassword())).thenReturn(passwordEncoded);

    Long userId = userService.updateUser(1L, userRequest);

    assertEquals(1L, userId);
  }

  @Test
  public void shouldReturnUserWhenGettingUserById() {
    UserEntity userEntity =
        UserEntity.builder()
            .id(1L)
            .role(roleEntity)
            .email("admin@admin.com")
            .password(passwordEncoded)
            .phoneNumber("+48123123123")
            .build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(userEntity))
        .thenReturn(
            User.builder()
                .id(1L)
                .role(role)
                .email("admin@admin.com")
                .password(passwordEncoded)
                .phoneNumber("+48123123123")
                .build());

    Optional<User> user = userService.getUserById(1L);

    assertTrue(user.isPresent());
    assertEquals(1L, user.get().getId());
    assertEquals("admin@admin.com", user.get().getEmail());
    assertEquals(passwordEncoded, user.get().getPassword());
    assertEquals("+48123123123", user.get().getPhoneNumber());
  }

  @Test
  public void shouldReturnEmptyWhenUserNotFoundById() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    Optional<User> userOptional = userService.getUserById(1L);

    assertTrue(userOptional.isEmpty());
  }

  @Test
  public void shouldReturnUserWhenGettingUserByEmail() {
    UserEntity userEntity =
        UserEntity.builder()
            .id(1L)
            .role(roleEntity)
            .email("admin@admin.com")
            .password(passwordEncoded)
            .phoneNumber("+48123123123")
            .build();

    when(userRepository.findByEmailIgnoreCase("admin@admin.com"))
        .thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(userEntity))
        .thenReturn(
            User.builder()
                .id(1L)
                .role(role)
                .email("admin@admin.com")
                .password(passwordEncoded)
                .phoneNumber("+48123123123")
                .build());

    Optional<User> user = userService.getUserByEmail("admin@admin.com");

    assertTrue(user.isPresent());
    assertEquals(1L, user.get().getId());
    assertEquals("admin@admin.com", user.get().getEmail());
    assertEquals(passwordEncoded, user.get().getPassword());
    assertEquals("+48123123123", user.get().getPhoneNumber());
  }

  @Test
  public void shouldReturnEmptyWhenUserNotFoundByEmail() {
    when(userRepository.findByEmailIgnoreCase("admin@admin.com")).thenReturn(Optional.empty());

    Optional<User> userOptional = userService.getUserByEmail("admin@admin.com");

    assertTrue(userOptional.isEmpty());
  }

  @Test
  public void shouldReturnPagedResponseWhenGettingAllUsers() {
    UserEntity userEntity =
        UserEntity.builder()
            .id(1L)
            .role(roleEntity)
            .email("admin@admin.com")
            .password(passwordEncoded)
            .phoneNumber("+48123123123")
            .build();
    Pageable pageable = PageRequest.of(0, 10);
    Page<UserEntity> page = new PageImpl<>(List.of(userEntity), pageable, 1);

    when(userRepository.findAll(pageable)).thenReturn(page);
    when(userMapper.fromEntity(userEntity))
        .thenReturn(
            User.builder()
                .id(1L)
                .role(role)
                .email("admin@admin.com")
                .password(passwordEncoded)
                .phoneNumber("+48123123123")
                .build());

    PagedResponse<User> pagedResponse = userService.getAllUsers(0, 10);

    assertEquals(1, pagedResponse.getTotalPages());
    assertEquals(1, pagedResponse.getContent().size());
    assertEquals(1L, pagedResponse.getContent().getFirst().getId());
    assertEquals(role, pagedResponse.getContent().getFirst().getRole());
    assertEquals("admin@admin.com", pagedResponse.getContent().getFirst().getEmail());
    assertEquals(passwordEncoded, pagedResponse.getContent().getFirst().getPassword());
    assertEquals("+48123123123", pagedResponse.getContent().getFirst().getPhoneNumber());
  }

  @Test
  public void shouldDeleteUserById() {
    when(userRepository.existsById(1L)).thenReturn(true);

    assertDoesNotThrow(() -> userService.deleteUser(1L));
  }

  @Test
  public void shouldThrowNotFoundExceptionWhenUserNotFoundOnDelete() {
    when(userRepository.existsById(1L)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> userService.deleteUser(1L));
  }

  @Test
  public void shouldThrowDeletionNotAllowedExceptionWhenDeletingUserWithDependencies() {
    when(userRepository.existsById(1L)).thenReturn(true);
    doThrow(
            new DataIntegrityViolationException(
                "", new ConstraintViolationException("", new SQLException(), "")))
        .when(userRepository)
        .deleteById(1L);

    assertThrows(DeletionNotAllowedException.class, () -> userService.deleteUser(1L));
  }

  @Test
  public void shouldThrowExceptionWhenDataIntegrityViolationIsNotCausedByConstraintViolation() {
    when(userRepository.existsById(1L)).thenReturn(true);
    doThrow(new DataIntegrityViolationException("", new SQLException()))
        .when(userRepository)
        .deleteById(1L);

    assertThrows(DataIntegrityViolationException.class, () -> userService.deleteUser(1L));
  }
}
