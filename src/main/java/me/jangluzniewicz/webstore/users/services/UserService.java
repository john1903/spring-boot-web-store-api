package me.jangluzniewicz.webstore.users.services;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.users.controllers.UserRequest;
import me.jangluzniewicz.webstore.users.entities.UserEntity;
import me.jangluzniewicz.webstore.users.interfaces.IRole;
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

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUser {
    private final UserRepository userRepository;
    private final IRole roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       UserMapper userMapper, IRole roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.roleService = roleService;
    }

    @Override
    @Transactional
    public Long registerNewUser(@NotNull UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
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
        return userRepository.save(userMapper.toEntity(user)).getId();
    }

    @Override
    @Transactional
    public Long updateUser(@Min(1) Long id, @NotNull UserRequest userRequest) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
        if (userRepository.existsByEmail(userRequest.getEmail()) &&
                !userEntity.getEmail().equals(userRequest.getEmail())) {
            throw new NotUniqueException("User with email " + userRequest.getEmail() + " already exists");
        }
        userEntity.setEmail(userRequest.getEmail());
        userEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userEntity.setPhoneNumber(userRequest.getPhoneNumber());
        return userEntity.getId();
    }

    @Override
    public Optional<User> getUserByEmail(@NotNull String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::fromEntity);
    }

    @Override
    public Optional<User> getUserById(@Min(1) Long id) {
        return userRepository.findById(id)
                .map(userMapper::fromEntity);
    }

    @Override
    public List<User> getAllUsers(@Min(1) Integer page, @Min(1) Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable).map(userMapper::fromEntity);
        return userPage.toList();
    }

    @Override
    @Transactional
    public void deleteUser(@Min(1) Long id) {
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
