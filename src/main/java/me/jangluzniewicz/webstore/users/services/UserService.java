package me.jangluzniewicz.webstore.users.services;

import java.util.Optional;
import me.jangluzniewicz.webstore.carts.interfaces.ICart;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.roles.interfaces.IRole;
import me.jangluzniewicz.webstore.users.controllers.UserRequest;
import me.jangluzniewicz.webstore.users.interfaces.IUser;
import me.jangluzniewicz.webstore.users.mappers.UserMapper;
import me.jangluzniewicz.webstore.users.models.User;
import me.jangluzniewicz.webstore.users.repositories.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
  public IdResponse registerNewUser(UserRequest userRequest) {
    if (userRepository.existsByEmailIgnoreCase(userRequest.getEmail())) {
      throw new NotUniqueException("User with email " + userRequest.getEmail() + " already exists");
    }
    User user =
        User.builder()
            .email(userRequest.getEmail())
            .phoneNumber(userRequest.getPhoneNumber())
            .password(passwordEncoder.encode(userRequest.getPassword()))
            .role(
                userRequest.getRoleId() != null
                    ? roleService
                        .getRoleById(userRequest.getRoleId())
                        .orElseThrow(
                            () ->
                                new NotFoundException(
                                    "Role with id " + userRequest.getRoleId() + " not found"))
                    : null)
            .build();
    Long userId = userRepository.save(userMapper.toEntity(user)).getId();
    cartService.createNewCart(userId);
    return new IdResponse(userId);
  }

  @Override
  @Transactional
  public void updateUser(Long id, UserRequest userRequest) {
    User user =
        getUserById(id)
            .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    if (userRepository.existsByEmailIgnoreCase(userRequest.getEmail())
        && !user.getEmail().equals(userRequest.getEmail())) {
      throw new NotUniqueException("User with email " + userRequest.getEmail() + " already exists");
    }
    final Long USER_ROLE_ID = 2L;
    Long roleId = Optional.ofNullable(userRequest.getRoleId()).orElse(USER_ROLE_ID);
    user.setRole(
        roleService
            .getRoleById(roleId)
            .orElseThrow(() -> new NotFoundException("Role with id " + roleId + " not found")));
    user.setEmail(userRequest.getEmail());
    user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
    user.setPhoneNumber(userRequest.getPhoneNumber());
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
    try {
      userRepository.deleteById(id);
    } catch (DataIntegrityViolationException e) {
      if (e.getCause() instanceof ConstraintViolationException) {
        throw new DeletionNotAllowedException(
            "User with id " + id + " cannot be deleted due to existing relations");
      } else {
        throw e;
      }
    }
  }
}
