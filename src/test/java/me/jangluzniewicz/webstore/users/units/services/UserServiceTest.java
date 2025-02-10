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
import me.jangluzniewicz.webstore.common.units.BaseServiceUnitTest;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.roles.interfaces.IRole;
import me.jangluzniewicz.webstore.users.controllers.UserRequest;
import me.jangluzniewicz.webstore.users.entities.UserEntity;
import me.jangluzniewicz.webstore.users.mappers.UserMapper;
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
class UserServiceTest extends BaseServiceUnitTest {
  @Mock private UserRepository userRepository;
  @Mock private IRole roleService;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private UserMapper userMapper;
  @Mock private ICart cartService;
  @InjectMocks private UserService userService;

  private UserRequest userRequest1;
  private UserRequest userRequest2;

  private final String encodedPassword =
      "$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC";

  @BeforeEach
  public void setUp() {
    userRequest1 = new UserRequest(1L, "admin@admin.com", "admin", "+48123123123");
    userRequest2 = new UserRequest(1L, "new@new.com", "admin", "+48123123123");
  }

  @Test
  void registerNewUser_whenEmailIsUniqueAndRoleExists_thenReturnUserId() {
    when(userRepository.existsByEmailIgnoreCase(userRequest1.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(userRequest1.getPassword())).thenReturn(encodedPassword);
    when(roleService.getRoleById(any())).thenReturn(Optional.of(role));
    when(userRepository.save(any())).thenReturn(userEntity);
    when(cartService.createNewCart(1L)).thenReturn(1L);

    assertEquals(1L, userService.registerNewUser(userRequest1));
  }

  @Test
  void registerNewUser_whenEmailAlreadyExists_thenThrowNotUniqueException() {
    when(userRepository.existsByEmailIgnoreCase(userRequest1.getEmail())).thenReturn(true);

    assertThrows(NotUniqueException.class, () -> userService.registerNewUser(userRequest1));
  }

  @Test
  void registerNewUser_whenRoleDoesNotExist_thenThrowNotFoundException() {
    when(userRepository.existsByEmailIgnoreCase(userRequest1.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(userRequest1.getPassword())).thenReturn(encodedPassword);
    when(roleService.getRoleById(any())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.registerNewUser(userRequest1));
  }

  @Test
  void updateUser_whenUserExistsAndEmailIsUnique_thenReturnUserId() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(any())).thenReturn(user);
    when(userRepository.existsByEmailIgnoreCase(userRequest2.getEmail())).thenReturn(false);
    when(roleService.getRoleById(any())).thenReturn(Optional.of(role));
    when(passwordEncoder.encode(userRequest2.getPassword())).thenReturn(encodedPassword);
    UserEntity entityUpdated =
        UserEntity.builder()
            .id(1L)
            .role(roleEntity)
            .email("new@new.com")
            .password(encodedPassword)
            .phoneNumber("+48123123123")
            .build();
    when(userRepository.save(any())).thenReturn(entityUpdated);

    assertEquals(1L, userService.updateUser(1L, userRequest2));
  }

  @Test
  void updateUser_whenUserDoesNotExist_thenThrowNotFoundException() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.updateUser(1L, userRequest2));
  }

  @Test
  void updateUser_whenEmailAlreadyExists_thenThrowNotUniqueException() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(any())).thenReturn(user);
    when(userRepository.existsByEmailIgnoreCase(userRequest2.getEmail())).thenReturn(true);

    assertThrows(NotUniqueException.class, () -> userService.updateUser(1L, userRequest2));
  }

  @Test
  void updateUser_whenEmailIsTheSame_thenDoNotThrowException() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(any())).thenReturn(user);
    when(userRepository.existsByEmailIgnoreCase(userRequest1.getEmail())).thenReturn(true);
    when(roleService.getRoleById(any())).thenReturn(Optional.of(role));
    when(passwordEncoder.encode(userRequest1.getPassword())).thenReturn(encodedPassword);
    when(userRepository.save(any())).thenReturn(userEntity);

    assertEquals(1L, userService.updateUser(1L, userRequest1));
  }

  @Test
  void updateUser_whenRoleDoesNotExist_thenThrowNotFoundException() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(any())).thenReturn(user);
    when(userRepository.existsByEmailIgnoreCase(userRequest1.getEmail())).thenReturn(false);
    when(roleService.getRoleById(any())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.updateUser(1L, userRequest1));
  }

  @Test
  void getUserById_whenUserExists_thenReturnUser() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(any())).thenReturn(user);

    assertTrue(userService.getUserById(1L).isPresent());
  }

  @Test
  void getUserById_whenUserDoesNotExist_thenReturnEmpty() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertTrue(userService.getUserById(1L).isEmpty());
  }

  @Test
  void getUserByEmail_whenUserExists_thenReturnUser() {
    when(userRepository.findByEmailIgnoreCase("admin@admin.com"))
        .thenReturn(Optional.of(userEntity));
    when(userMapper.fromEntity(any())).thenReturn(user);

    assertTrue(userService.getUserByEmail("admin@admin.com").isPresent());
  }

  @Test
  void getUserByEmail_whenUserDoesNotExist_thenReturnEmpty() {
    when(userRepository.findByEmailIgnoreCase("admin@admin.com")).thenReturn(Optional.empty());

    assertTrue(userService.getUserByEmail("admin@admin.com").isEmpty());
  }

  @Test
  void getAllUsers_whenUsersExist_thenReturnPagedResponse() {
    when(userRepository.findAll(any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(userEntity)));
    when(userMapper.fromEntity(any())).thenReturn(user);

    assertEquals(1, userService.getAllUsers(0, 10).getTotalPages());
  }

  @Test
  void deleteUser_whenUserExists_thenDeleteSuccessfully() {
    when(userRepository.existsById(1L)).thenReturn(true);

    assertDoesNotThrow(() -> userService.deleteUser(1L));
  }

  @Test
  void deleteUser_whenUserDoesNotExist_thenThrowNotFoundException() {
    when(userRepository.existsById(1L)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> userService.deleteUser(1L));
  }

  @Test
  void deleteUser_whenUserHasDependencies_thenThrowDeletionNotAllowedException() {
    when(userRepository.existsById(1L)).thenReturn(true);
    doThrow(
            new DataIntegrityViolationException(
                "", new ConstraintViolationException("", new SQLException(), "")))
        .when(userRepository)
        .deleteById(1L);

    assertThrows(DeletionNotAllowedException.class, () -> userService.deleteUser(1L));
  }

  @Test
  void deleteUser_whenDataIntegrityViolationIsNotConstraintRelated_thenThrowException() {
    when(userRepository.existsById(1L)).thenReturn(true);
    doThrow(new DataIntegrityViolationException("", new SQLException()))
        .when(userRepository)
        .deleteById(1L);

    assertThrows(DataIntegrityViolationException.class, () -> userService.deleteUser(1L));
  }
}
