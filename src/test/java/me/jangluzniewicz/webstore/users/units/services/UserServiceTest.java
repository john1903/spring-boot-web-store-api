package me.jangluzniewicz.webstore.users.units.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.carts.interfaces.ICart;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.roles.interfaces.IRole;
import me.jangluzniewicz.webstore.roles.models.Role;
import me.jangluzniewicz.webstore.users.controllers.UserRequest;
import me.jangluzniewicz.webstore.users.entities.UserEntity;
import me.jangluzniewicz.webstore.users.mappers.UserMapper;
import me.jangluzniewicz.webstore.users.models.User;
import me.jangluzniewicz.webstore.users.repositories.UserRepository;
import me.jangluzniewicz.webstore.users.services.UserService;
import me.jangluzniewicz.webstore.utils.testdata.roles.RoleTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.users.UserEntityTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.users.UserRequestTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.users.UserTestDataBuilder;
import me.jangluzniewicz.webstore.utils.units.config.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest extends UnitTest {
  @Mock private UserRepository userRepository;
  @Mock private IRole roleService;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private UserMapper userMapper;
  @Mock private ICart cartService;
  @InjectMocks private UserService userService;

  private UserEntity userEntity;
  private User user;
  private Role role;
  private UserRequest userRequest1;
  private UserRequest userRequest2;

  @BeforeEach
  public void setUp() {
    userEntity = UserEntityTestDataBuilder.builder().build().buildUserEntity();
    user = UserTestDataBuilder.builder().build().buildUser();
    role = RoleTestDataBuilder.builder().build().buildRole();
    userRequest1 = UserRequestTestDataBuilder.builder().build().buildUserRequest();
    userRequest2 =
        UserRequestTestDataBuilder.builder().email("new@new.com").build().buildUserRequest();
  }

  @Test
  void registerNewUser_whenEmailIsUniqueAndRoleExists_thenReturnIdResponse() {
    when(userRepository.existsByEmailIgnoreCase(userRequest1.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(userRequest1.getPassword())).thenReturn(userEntity.getPassword());
    when(roleService.getRoleById(userRequest1.getRoleId())).thenReturn(Optional.of(role));
    when(userRepository.save(any())).thenReturn(userEntity);
    when(cartService.createNewCart(userEntity.getId())).thenReturn(new IdResponse(1L));

    assertEquals(userEntity.getId(), userService.registerNewUser(userRequest1).getId());
  }

  @Test
  void registerNewUser_whenEmailAlreadyExists_thenThrowNotUniqueException() {
    when(userRepository.existsByEmailIgnoreCase(userRequest1.getEmail())).thenReturn(true);

    assertThrows(NotUniqueException.class, () -> userService.registerNewUser(userRequest1));
  }

  @Test
  void registerNewUser_whenRoleDoesNotExist_thenThrowNotFoundException() {
    when(userRepository.existsByEmailIgnoreCase(userRequest1.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(userRequest1.getPassword())).thenReturn(userEntity.getPassword());
    when(roleService.getRoleById(any())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.registerNewUser(userRequest1));
  }

  @Test
  void registerNewUser_whenRoleIsNull_thenReturnIdResponse() {
    when(userRepository.existsByEmailIgnoreCase(userRequest1.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(userRequest1.getPassword())).thenReturn(userEntity.getPassword());
    when(userRepository.save(any())).thenReturn(userEntity);
    when(cartService.createNewCart(userEntity.getId()))
        .thenReturn(new IdResponse(userEntity.getId()));

    assertEquals(
        userEntity.getId(),
        userService
            .registerNewUser(
                UserRequestTestDataBuilder.builder().roleId(null).build().buildUserRequest())
            .getId());
  }

  @Test
  void updateUser_whenUserExistsAndEmailIsUnique_thenUpdateUser() {
    when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(userEntity)).thenReturn(user);
    when(userRepository.existsByEmailIgnoreCase(userRequest2.getEmail())).thenReturn(false);
    when(roleService.getRoleById(userRequest1.getRoleId())).thenReturn(Optional.of(role));
    when(passwordEncoder.encode(userRequest2.getPassword())).thenReturn(userEntity.getPassword());
    UserEntity entityUpdated =
        UserEntityTestDataBuilder.builder()
            .email(userRequest2.getEmail())
            .build()
            .buildUserEntity();
    when(userRepository.save(any())).thenReturn(entityUpdated);

    assertDoesNotThrow(() -> userService.updateUser(userEntity.getId(), userRequest2));
  }

  @Test
  void updateUser_whenUserDoesNotExist_thenThrowNotFoundException() {
    when(userRepository.findById(userEntity.getId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class, () -> userService.updateUser(userEntity.getId(), userRequest2));
  }

  @Test
  void updateUser_whenEmailAlreadyExists_thenThrowNotUniqueException() {
    when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(userEntity)).thenReturn(user);
    when(userRepository.existsByEmailIgnoreCase(userRequest2.getEmail())).thenReturn(true);

    assertThrows(
        NotUniqueException.class, () -> userService.updateUser(userEntity.getId(), userRequest2));
  }

  @Test
  void updateUser_whenEmailIsTheSame_thenDoNotThrowException() {
    when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(userEntity)).thenReturn(user);
    when(userRepository.existsByEmailIgnoreCase(userRequest1.getEmail())).thenReturn(true);
    when(roleService.getRoleById(userRequest1.getRoleId())).thenReturn(Optional.of(role));
    when(passwordEncoder.encode(userRequest1.getPassword())).thenReturn(userEntity.getPassword());
    when(userRepository.save(any())).thenReturn(userEntity);

    assertDoesNotThrow(() -> userService.updateUser(userEntity.getId(), userRequest1));
  }

  @Test
  void updateUser_whenRoleDoesNotExist_thenThrowNotFoundException() {
    when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(userEntity)).thenReturn(user);
    when(userRepository.existsByEmailIgnoreCase(userRequest1.getEmail())).thenReturn(false);
    when(roleService.getRoleById(userRequest1.getRoleId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class, () -> userService.updateUser(userEntity.getId(), userRequest1));
  }

  @Test
  void getUserById_whenUserExists_thenReturnUser() {
    when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(userEntity)).thenReturn(user);

    assertTrue(userService.getUserById(userEntity.getId()).isPresent());
  }

  @Test
  void getUserById_whenUserDoesNotExist_thenReturnEmpty() {
    when(userRepository.findById(userEntity.getId())).thenReturn(Optional.empty());

    assertTrue(userService.getUserById(userEntity.getId()).isEmpty());
  }

  @Test
  void getUserByEmail_whenUserExists_thenReturnUser() {
    when(userRepository.findByEmailIgnoreCase(userEntity.getEmail()))
        .thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(userEntity)).thenReturn(user);

    assertTrue(userService.getUserByEmail(userEntity.getEmail()).isPresent());
  }

  @Test
  void getUserByEmail_whenUserDoesNotExist_thenReturnEmpty() {
    when(userRepository.findByEmailIgnoreCase(userEntity.getEmail())).thenReturn(Optional.empty());

    assertTrue(userService.getUserByEmail(userEntity.getEmail()).isEmpty());
  }

  @Test
  void getAllUsers_whenUsersExist_thenReturnPagedResponse() {
    when(userRepository.findAll(any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(userEntity)));
    when(userMapper.fromEntity(userEntity)).thenReturn(user);

    assertEquals(1, userService.getAllUsers(0, 10).getTotalPages());
  }

  @Test
  void deleteUser_whenUserExists_thenDeleteSuccessfully() {
    when(userRepository.existsById(userEntity.getId())).thenReturn(true);

    assertDoesNotThrow(() -> userService.deleteUser(userEntity.getId()));
  }

  @Test
  void deleteUser_whenUserDoesNotExist_thenThrowNotFoundException() {
    when(userRepository.existsById(userEntity.getId())).thenReturn(false);

    assertThrows(NotFoundException.class, () -> userService.deleteUser(userEntity.getId()));
  }
}
