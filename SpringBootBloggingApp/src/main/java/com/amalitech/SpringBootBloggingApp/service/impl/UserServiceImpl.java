package com.amalitech.SpringBootBloggingApp.service.impl;

import com.amalitech.SpringBootBloggingApp.cache.Cache;
import com.amalitech.SpringBootBloggingApp.model.dto.request.LoginRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.request.RegisterRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.request.UpdateUserDetailRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.request.UpdateUserRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.UserResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.repository.UserRepository;
import com.amalitech.SpringBootBloggingApp.service.UserService;

import com.amalitech.SpringBootBloggingApp.util.JwtUtil;
import com.amalitech.SpringBootBloggingApp.util.ValidationUtil;
import com.amalitech.SpringBootBloggingApp.util.exceptions.UserInputsException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.amalitech.SpringBootBloggingApp.util.PasswordUtil.hashPassword;
import static com.amalitech.SpringBootBloggingApp.util.PasswordUtil.verifyPassword;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Cache<String, User> userCache;

    public UserServiceImpl(UserRepository userRepository, Cache<String, User> userCache) {
        this.userRepository = userRepository;
        this.userCache = userCache;
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserResponse create(RegisterRequest user) {
        if (!ValidationUtil.isUsernameValid(user.getUsername())) {
            throw new UserInputsException("Username is not valid");
        }
        if (!ValidationUtil.isEmailValid(user.getEmail())) {
            throw new UserInputsException("Email is not valid");
        }
        if (!ValidationUtil.isPasswordValid(user.getPassword())) {
            throw new UserInputsException("Password is not valid");
        }
        User registerUser = new User(
                null,
                user.getUsername(),
                user.getEmail(),
                hashPassword(user.getPassword()),
                System.currentTimeMillis(),
                null
        );
        User saved = userRepository.save(registerUser);
        if (saved != null) {
            userCache.put(saved.getId(), saved);
        }
        String token = JwtUtil.generateToken(saved.getId(), user.getEmail());
        return new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                token
        );

    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest user) {
        if (userRepository.findByEmail(user.getEmail()).isEmpty()){
            throw new UserInputsException("Email not found");
        } else {
            User loggedIn = userRepository.findByEmail(user.getEmail()).get();
            if (!verifyPassword(user.getPassword(), loggedIn.getPasswordHash())) {
                throw new UserInputsException("Password is not valid");
            }
            String token = JwtUtil.generateToken(loggedIn.getId(), loggedIn.getEmail());
            return new UserResponse(
                    loggedIn.getId(),
                    loggedIn.getUsername(),
                    loggedIn.getEmail(),
                    loggedIn.getCreatedAt(),
                    loggedIn.getUpdatedAt(),
                    token
            );
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserResponse update(UpdateUserRequest user) {
        if (!ValidationUtil.isUsernameValid(user.getUsername())) {
            throw new UserInputsException("Username is not valid");
        }
        if (!ValidationUtil.isEmailValid(user.getEmail())) {
            throw new UserInputsException("Email is not valid");
        }
        if (!ValidationUtil.isPasswordValid(user.getPassword())) {
            throw new UserInputsException("Password is not valid");
        }
        User foundUser = userRepository.findById(user.getId()).orElse(null);
        if (foundUser == null) {
            throw new UserInputsException("User not found");
        }
        User updateUser = new User(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                hashPassword(user.getPassword()),
                foundUser.getCreatedAt(),
                System.currentTimeMillis()
        );
        User saved = userRepository.save(updateUser);
        if (saved != null) {
            userCache.put(saved.getId(), saved);
            String token = JwtUtil.generateToken(saved.getId(), saved.getEmail());
            return new UserResponse(
                    saved.getId(),
                    saved.getUsername(),
                    saved.getEmail(),
                    saved.getCreatedAt(),
                    saved.getUpdatedAt(),
                    token
            );
        }
        return null;
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public boolean delete(String userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserInputsException("User not found");
        }
        userRepository.deleteById(userId);
        userCache.remove(userId);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#userId")
    public UserResponse getById(String userId) {
        User foundUser = userCache.get(userId);
        if (foundUser == null) {
            foundUser = userRepository.findById(userId).orElse(null);
            if (foundUser == null) {
                throw new UserInputsException("User not found");
            }
            userCache.put(foundUser.getId(), foundUser);
        }
        String token = JwtUtil.generateToken(foundUser.getId(), foundUser.getEmail());
        return new UserResponse(
                foundUser.getId(),
                foundUser.getUsername(),
                foundUser.getEmail(),
                foundUser.getCreatedAt(),
                foundUser.getUpdatedAt(),
                token
        );
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#email")
    public UserResponse getByEmail(String email) {
        User foundUser = userCache.get(email);
        if (foundUser == null) {
            foundUser = userRepository.findByEmail(email).orElse(null);
            if (foundUser == null) {
                throw new UserInputsException("User not found");
            }
            userCache.put(foundUser.getEmail(), foundUser);
        }
        String token = JwtUtil.generateToken(foundUser.getId(), foundUser.getEmail());
        return new UserResponse(
                foundUser.getId(),
                foundUser.getUsername(),
                foundUser.getEmail(),
                foundUser.getCreatedAt(),
                foundUser.getUpdatedAt(),
                token
        );
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "'all'")
    public List<UserResponse> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> {
            String token = JwtUtil.generateToken(user.getId(), user.getEmail());
            return new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    token
            );
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<User> getAllPaginated(int page, int size) {
        long total = userRepository.count();
        var pageable = PageRequest.of(page, size);
        var pageResult = userRepository.findAll(pageable);
        return new PageResponse<>(pageResult.getContent(), page, size, total);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public boolean updateUserDetails(String userId, UpdateUserDetailRequest user) {
        if (!ValidationUtil.isValidObjectId(userId)) {
            throw new UserInputsException("User ID is not valid");
        }
        if (!ValidationUtil.isUsernameValid(user.getUsername())) {
            throw new UserInputsException("Username is not valid");
        }
        if (!ValidationUtil.isEmailValid(user.getEmail())) {
            throw new UserInputsException("Email is not valid");
        }
        User existingUser = userRepository.findById(userId).orElseThrow(
            () -> new UserInputsException("User not found")
        );
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setUpdatedAt(System.currentTimeMillis());
        
        User saved = userRepository.save(existingUser);
        if (saved != null) {
            userCache.put(saved.getId(), saved);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        if (!ValidationUtil.isValidObjectId(userId)) {
            throw new UserInputsException("User ID is not valid");
        }
        User user = userCache.get(userId);
        if (user == null) {
            user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                throw new UserInputsException("User not found");
            }
            userCache.put(user.getId(), user);
        }
        if (user == null || !verifyPassword(oldPassword, user.getPasswordHash())) {
            throw new UserInputsException("Old password is not valid");
        }
        user.setPasswordHash(hashPassword(newPassword));
        user.setUpdatedAt(System.currentTimeMillis());
        User saved = userRepository.save(user);
        if (saved != null) {
            userCache.put(saved.getId(), saved);
            return true;
        }
        return false;
    }

    @Override
    public UserResponse getByUsername(String username) {
        if (!ValidationUtil.isUsernameValid(username)) {
            throw new UserInputsException("Username is not valid");
        }
        User foundUser = userRepository.findByUsername(username).orElse(null);
        if (foundUser == null) {
            throw new UserInputsException("User not found");
        }
        String token = JwtUtil.generateToken(foundUser.getId(), foundUser.getEmail());
        return new UserResponse(
                foundUser.getId(),
                foundUser.getUsername(),
                foundUser.getEmail(),
                foundUser.getCreatedAt(),
                foundUser.getUpdatedAt(),
                token
        );
    }
}

