package me.jangluzniewicz.webstore.users.services;

import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.IdViolationException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.users.entities.UserEntity;
import me.jangluzniewicz.webstore.users.mappers.UserMapper;
import me.jangluzniewicz.webstore.users.models.User;
import me.jangluzniewicz.webstore.users.repositories.RoleRepository;
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
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       UserMapper userMapper, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public Long registerNewUser(User user) {
        if (user.getId() != null && userRepository.existsById(user.getId())) {
            throw new IdViolationException("User with id " + user.getId() + " already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new NotUniqueException("User with email " + user.getEmail() + " already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(userMapper.toEntity(user)).getId();
    }

    @Transactional
    public Long updateUser (Long id, User user) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
        if (userRepository.existsByEmail(user.getEmail()) && !userEntity.getEmail().equals(user.getEmail())) {
            throw new NotUniqueException("User with email " + user.getEmail() + " already exists");
        }
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        userEntity.setRole(roleRepository.findById(user.getRole().getId())
                .orElseThrow(() -> new NotFoundException("Role with id " + user.getRole().getId() + " not found")));
        return userRepository.save(userEntity).getId();
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::fromEntity);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::fromEntity);
    }

    public List<User> getAllUsers(int page, int size) {
        if (page < 0 || size < 1) {
            throw new IllegalArgumentException("Page must be greater than or equal to 0 and size must be greater than 0");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable).map(userMapper::fromEntity);
        return userPage.toList();
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new DeletionNotAllowedException("User with id " + id + " cannot be deleted due to existing relations");
            }
        }
    }
}
