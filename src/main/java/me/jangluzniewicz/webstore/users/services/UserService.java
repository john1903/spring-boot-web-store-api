package me.jangluzniewicz.webstore.users.services;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.jangluzniewicz.webstore.carts.interfaces.ICart;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.users.controllers.UserRequest;
import me.jangluzniewicz.webstore.users.entities.UserEntity;
import me.jangluzniewicz.webstore.roles.interfaces.IRole;
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

import java.util.Optional;

@Service
public class UserService implements IUser {
    private final UserRepository userRepository;
    private final IRole roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ICart cartService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       UserMapper userMapper, IRole roleService, ICart cartService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.roleService = roleService;
        this.cartService = cartService;
    }

    @Override
    @Transactional
    public Long registerNewUser(@NotNull UserRequest userRequest) {
        if (userRepository.existsByEmailIgnoreCase(userRequest.getEmail())) {
            throw new NotUniqueException("User with email " + userRequest.getEmail() + " already exists");
        }
        User user = User.builder()
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .role(roleService.getRoleById(userRequest.getRoleId())
                        .orElseThrow(() ->
                                new NotFoundException("Role with id " + userRequest.getRoleId() + " not found")))
                .build();
        user.setId(userRepository.save(userMapper.toEntity(user)).getId());
        cartService.createNewCart(user);
        return user.getId();
    }

    @Override
    @Transactional
    public Long updateUser(@NotNull @Min(1) Long id, @NotNull UserRequest userRequest) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
        if (userRepository.existsByEmailIgnoreCase(userRequest.getEmail()) &&
                !userEntity.getEmail().equals(userRequest.getEmail())) {
            throw new NotUniqueException("User with email " + userRequest.getEmail() + " already exists");
        }
        userEntity.setEmail(userRequest.getEmail());
        userEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userEntity.setPhoneNumber(userRequest.getPhoneNumber());
        return userEntity.getId();
    }

    @Override
    public Optional<User> getUserByEmail(@NotNull @Size(min = 5, max = 255) String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .map(userMapper::fromEntity);
    }

    @Override
    public Optional<User> getUserById(@NotNull @Min(1) Long id) {
        return userRepository.findById(id)
                .map(userMapper::fromEntity);
    }

    @Override
    public PagedResponse<User> getAllUsers(@NotNull @Min(0) Integer page, @NotNull @Min(1) Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable).map(userMapper::fromEntity);
        return new PagedResponse<>(users.getTotalPages(), users.toList());
    }

    @Override
    @Transactional
    public void deleteUser(@NotNull @Min(1) Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new DeletionNotAllowedException("User with id " + id +
                        " cannot be deleted due to existing relations");
            }
        }
    }
}
