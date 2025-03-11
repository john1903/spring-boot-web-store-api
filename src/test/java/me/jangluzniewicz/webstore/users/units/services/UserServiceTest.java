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
import me.jangluzniewicz.webstore.users.controllers.ChangeUserPasswordRequest;
import me.jangluzniewicz.webstore.users.controllers.CreateUserRequest;
import me.jangluzniewicz.webstore.users.controllers.UpdateUserRequest;
import me.jangluzniewicz.webstore.users.entities.UserEntity;
import me.jangluzniewicz.webstore.users.mappers.UserMapper;
import me.jangluzniewicz.webstore.users.models.User;
import me.jangluzniewicz.webstore.users.repositories.UserRepository;
import me.jangluzniewicz.webstore.users.services.UserService;
import me.jangluzniewicz.webstore.utils.testdata.roles.RoleTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.users.*;
import me.jangluzniewicz.webstore.utils.units.config.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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
  private CreateUserRequest createUserRequest1;
  private UpdateUserRequest updateUserRequest1;
  private ChangeUserPasswordRequest changeUserPasswordRequest;

  @BeforeEach
  public void setUp() {
    userEntity = UserEntityTestDataBuilder.builder().build().buildUserEntity();
    user = UserTestDataBuilder.builder().build().buildUser();
    role = RoleTestDataBuilder.builder().build().buildRole();
    createUserRequest1 = CreateUserRequestTestDataBuilder.builder().build().buildUserRequest();
    updateUserRequest1 =
        UpdateUserRequestTestDataBuilder.builder().email("new@new.com").build().buildUserRequest();
    changeUserPasswordRequest =
        ChangeUserPasswordRequestTestDataBuilder.builder().build().buildUserRequest();
  }

  @Test
  void registerNewUser_whenEmailIsUniqueAndRoleExists_thenReturnIdResponse() {
    when(userRepository.existsByEmailIgnoreCase(createUserRequest1.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(createUserRequest1.getPassword()))
        .thenReturn(userEntity.getPassword());
    when(roleService.getRoleById(createUserRequest1.getRoleId())).thenReturn(Optional.of(role));
    when(userRepository.save(any())).thenReturn(userEntity);
    when(cartService.createNewCart(userEntity.getId())).thenReturn(new IdResponse(1L));

    assertEquals(userEntity.getId(), userService.registerNewUser(createUserRequest1).getId());
  }

  @Test
  void registerNewUser_whenEmailAlreadyExists_thenThrowNotUniqueException() {
    when(userRepository.existsByEmailIgnoreCase(createUserRequest1.getEmail())).thenReturn(true);

    assertThrows(NotUniqueException.class, () -> userService.registerNewUser(createUserRequest1));
  }

  @Test
  void registerNewUser_whenRoleDoesNotExist_thenThrowNotFoundException() {
    when(userRepository.existsByEmailIgnoreCase(createUserRequest1.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(createUserRequest1.getPassword()))
        .thenReturn(userEntity.getPassword());
    when(roleService.getRoleById(any())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.registerNewUser(createUserRequest1));
  }

  @Test
  void registerNewUser_whenRoleIsNull_thenReturnIdResponse() {
    when(userRepository.existsByEmailIgnoreCase(createUserRequest1.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(createUserRequest1.getPassword()))
        .thenReturn(userEntity.getPassword());
    when(userRepository.save(any())).thenReturn(userEntity);
    when(cartService.createNewCart(userEntity.getId()))
        .thenReturn(new IdResponse(userEntity.getId()));

    assertEquals(
        userEntity.getId(),
        userService
            .registerNewUser(
                CreateUserRequestTestDataBuilder.builder().roleId(null).build().buildUserRequest())
            .getId());
  }

  @Test
  void updateUser_whenUserExistsAndEmailIsUnique_thenUpdateUser() {
    when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(userEntity)).thenReturn(user);
    when(userRepository.existsByEmailIgnoreCase(updateUserRequest1.getEmail())).thenReturn(false);
    when(roleService.getRoleById(updateUserRequest1.getRoleId())).thenReturn(Optional.of(role));
    UserEntity entityUpdated =
        UserEntityTestDataBuilder.builder()
            .email(updateUserRequest1.getEmail())
            .build()
            .buildUserEntity();
    when(userRepository.save(any())).thenReturn(entityUpdated);

    assertDoesNotThrow(() -> userService.updateUser(userEntity.getId(), updateUserRequest1));
  }

  @Test
  void updateUser_whenUserDoesNotExist_thenThrowNotFoundException() {
    when(userRepository.findById(userEntity.getId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> userService.updateUser(userEntity.getId(), updateUserRequest1));
  }

  @Test
  void updateUser_whenEmailAlreadyExists_thenThrowNotUniqueException() {
    UpdateUserRequest updateUserRequestWithExistingEmail =
        UpdateUserRequestTestDataBuilder.builder()
            .email("email@email.com")
            .build()
            .buildUserRequest();
    when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(userEntity)).thenReturn(user);
    when(userRepository.existsByEmailIgnoreCase(updateUserRequestWithExistingEmail.getEmail()))
        .thenReturn(true);

    assertThrows(
        NotUniqueException.class,
        () -> userService.updateUser(userEntity.getId(), updateUserRequestWithExistingEmail));
  }

  @Test
  void updateUser_whenEmailIsTheSame_thenDoNotThrowException() {
    UpdateUserRequest updateUserRequestWithSameEmail =
        UpdateUserRequestTestDataBuilder.builder().build().buildUserRequest();

    when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(userEntity)).thenReturn(user);
    when(roleService.getRoleById(updateUserRequestWithSameEmail.getRoleId()))
        .thenReturn(Optional.of(role));
    when(userRepository.save(any())).thenReturn(userEntity);

    assertDoesNotThrow(
        () -> userService.updateUser(userEntity.getId(), updateUserRequestWithSameEmail));
  }

  @Test
  void updateUser_whenRoleDoesNotExist_thenThrowNotFoundException() {
    when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(userEntity)).thenReturn(user);
    when(roleService.getRoleById(updateUserRequest1.getRoleId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> userService.updateUser(userEntity.getId(), updateUserRequest1));
  }

  @Test
  void changePassword_whenUserExistsAndCurrentPasswordIsCorrect_thenChangePassword() {
    when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(userEntity)).thenReturn(user);
    when(passwordEncoder.matches(
            changeUserPasswordRequest.getCurrentPassword(), userEntity.getPassword()))
        .thenReturn(true);
    when(passwordEncoder.encode(changeUserPasswordRequest.getNewPassword()))
        .thenReturn(userEntity.getPassword());
    when(userRepository.save(any())).thenReturn(userEntity);

    assertDoesNotThrow(
        () -> userService.changePassword(userEntity.getId(), changeUserPasswordRequest));
  }

  @Test
  void changePassword_whenUserDoesNotExist_thenThrowNotFoundException() {
    when(userRepository.findById(userEntity.getId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> userService.changePassword(userEntity.getId(), changeUserPasswordRequest));
  }

  @Test
  void changePassword_whenCurrentPasswordIsIncorrect_thenThrowAccessDeniedException() {
    when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(userEntity)).thenReturn(user);
    when(passwordEncoder.matches(
            changeUserPasswordRequest.getCurrentPassword(), userEntity.getPassword()))
        .thenReturn(false);

    assertThrows(
        AccessDeniedException.class,
        () -> userService.changePassword(userEntity.getId(), changeUserPasswordRequest));
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
