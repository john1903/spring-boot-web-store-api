package me.jangluzniewicz.webstore.users.services;

import java.util.Optional;
import me.jangluzniewicz.webstore.carts.interfaces.ICart;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.roles.interfaces.IRole;
import me.jangluzniewicz.webstore.users.controllers.ChangeUserPasswordRequest;
import me.jangluzniewicz.webstore.users.controllers.CreateUserRequest;
import me.jangluzniewicz.webstore.users.controllers.UpdateUserRequest;
import me.jangluzniewicz.webstore.users.interfaces.IUser;
import me.jangluzniewicz.webstore.users.mappers.UserMapper;
import me.jangluzniewicz.webstore.users.models.User;
import me.jangluzniewicz.webstore.users.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class UserService implements IUser {
  private final UserRepository userRepository;
  private final IRole roleService;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final ICart cartService;

  public UserService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      UserMapper userMapper,
      IRole roleService,
      ICart cartService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.userMapper = userMapper;
    this.roleService = roleService;
    this.cartService = cartService;
  }

  @Override
  @Transactional
  public IdResponse registerNewUser(CreateUserRequest createUserRequest) {
    if (userRepository.existsByEmailIgnoreCase(createUserRequest.getEmail())) {
      throw new NotUniqueException(
          "User with email " + createUserRequest.getEmail() + " already exists");
    }
    User user =
        User.builder()
            .email(createUserRequest.getEmail())
            .phoneNumber(createUserRequest.getPhoneNumber())
            .password(passwordEncoder.encode(createUserRequest.getPassword()))
            .role(
                createUserRequest.getRoleId() != null
                    ? roleService
                        .getRoleById(createUserRequest.getRoleId())
                        .orElseThrow(
                            () ->
                                new NotFoundException(
                                    "Role with id " + createUserRequest.getRoleId() + " not found"))
                    : null)
            .build();
    Long userId = userRepository.save(userMapper.toEntity(user)).getId();
    cartService.createNewCart(userId);
    return new IdResponse(userId);
  }

  @Override
  @Transactional
  public void updateUser(Long id, UpdateUserRequest updateUserRequest) {
    User user =
        getUserById(id)
            .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    Optional.ofNullable(updateUserRequest.getEmail())
        .filter(email -> !user.getEmail().equals(email))
        .ifPresent(
            email -> {
              if (userRepository.existsByEmailIgnoreCase(email)) {
                throw new NotUniqueException("User with email " + email + " already exists");
              }
              user.setEmail(email);
            });
    Optional.ofNullable(updateUserRequest.getPhoneNumber()).ifPresent(user::setPhoneNumber);
    final Long USER_ROLE_ID = 2L;
    Long roleId = Optional.ofNullable(updateUserRequest.getRoleId()).orElse(USER_ROLE_ID);
    user.setRole(
        roleService
            .getRoleById(roleId)
            .orElseThrow(() -> new NotFoundException("Role with id " + roleId + " not found")));
    userRepository.save(userMapper.toEntity(user));
  }

  @Override
  public void changePassword(Long id, ChangeUserPasswordRequest changeUserPasswordRequest) {
    User user =
        getUserById(id)
            .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    if (!passwordEncoder.matches(
        changeUserPasswordRequest.getCurrentPassword(), user.getPassword())) {
      throw new AccessDeniedException("Invalid current password");
    }
    user.setPassword(passwordEncoder.encode(changeUserPasswordRequest.getNewPassword()));
    userRepository.save(userMapper.toEntity(user));
  }

  @Override
  public Optional<User> getUserByEmail(String email) {
    return userRepository.findByEmailIgnoreCase(email).map(userMapper::fromEntity);
  }

  @Override
  public Optional<User> getUserById(Long id) {
    return userRepository.findById(id).map(userMapper::fromEntity);
  }

  @Override
  public PagedResponse<User> getAllUsers(Integer page, Integer size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<User> users = userRepository.findAll(pageable).map(userMapper::fromEntity);
    return new PagedResponse<>(users.getTotalPages(), users.toList());
  }

  @Override
  @Transactional
  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new NotFoundException("User with id " + id + " not found");
    }
    userRepository.deleteById(id);
  }
}
