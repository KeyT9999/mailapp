package com.outlookmail.mailapp.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outlookmail.mailapp.dto.PasswordChangeRequest;
import com.outlookmail.mailapp.dto.ProfileUpdateRequest;
import com.outlookmail.mailapp.model.User;
import com.outlookmail.mailapp.repository.UserRepository;
import com.outlookmail.mailapp.util.PasswordUtil;

@Service
@Transactional
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public boolean authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return PasswordUtil.checkPassword(password, user.getPasswordHash());
        }
        return false;
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User createUser(String email, String password, String role) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        String passwordHash = PasswordUtil.hashPassword(password);
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setRole(role);
        
        return userRepository.save(user);
    }
    
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    public void updatePassword(Long userId, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPasswordHash(PasswordUtil.hashPassword(newPassword));
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }
    
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    // Profile Management Methods
    public User updateProfile(User user, ProfileUpdateRequest request) {
        logger.info("Updating profile for user: {}, request: {}", user.getEmail(), request);
        
        // Update display name (allow empty to clear)
        if (request.getDisplayName() != null) {
            String displayName = request.getDisplayName().trim();
            user.setDisplayName(displayName.isEmpty() ? null : displayName);
            logger.debug("Updated display name: {}", user.getDisplayName());
        }
        
        // Update username (allow empty to clear)
        if (request.getUsername() != null) {
            String username = request.getUsername().trim();
            user.setUsername(username.isEmpty() ? null : username);
            logger.debug("Updated username: {}", user.getUsername());
        }
        
        // Update avatar URL (allow empty to clear)
        if (request.getAvatarUrl() != null) {
            String avatarUrl = request.getAvatarUrl().trim();
            user.setAvatarUrl(avatarUrl.isEmpty() ? null : avatarUrl);
            logger.debug("Updated avatar URL: {}", user.getAvatarUrl());
        }
        
        User savedUser = userRepository.save(user);
        logger.info("Profile updated successfully for user: {}", user.getEmail());
        return savedUser;
    }
    
    public boolean changePassword(User user, PasswordChangeRequest request, PasswordEncoder passwordEncoder) {
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            return false;
        }
        
        // Update password
        String newPasswordHash = passwordEncoder.encode(request.getNewPassword());
        user.setPasswordHash(newPasswordHash);
        userRepository.save(user);
        
        return true;
    }
} 