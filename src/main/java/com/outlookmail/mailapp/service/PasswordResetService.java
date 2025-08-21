package com.outlookmail.mailapp.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outlookmail.mailapp.model.PasswordResetToken;
import com.outlookmail.mailapp.model.User;
import com.outlookmail.mailapp.repository.PasswordResetTokenRepository;
import com.outlookmail.mailapp.util.PasswordUtil;

@Service
@Transactional
public class PasswordResetService {
    
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);
    
    private final PasswordResetTokenRepository tokenRepository;
    private final UserService userService;
    private final EmailService emailService;
    
    // Token expires in 1 hour
    private static final int TOKEN_EXPIRY_HOURS = 1;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${server.servlet.context-path:}")
    private String contextPath;
    
    @Autowired
    public PasswordResetService(PasswordResetTokenRepository tokenRepository, 
                               UserService userService,
                               EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.userService = userService;
        this.emailService = emailService;
    }
    
    public boolean sendPasswordResetEmail(String email) {
        try {
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                logger.warn("Password reset requested for non-existent email: {}", email);
                return false;
            }
            
            User user = userOpt.get();
            
            // Invalidate any existing tokens for this user
            invalidateExistingTokens(user.getEmail());
            
            // Create new token
            String token = generateToken();
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS);
            
            PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
            tokenRepository.save(resetToken);
            
            // Generate reset link
            String resetLink = generateResetLink(token);
            
            // Send email
            emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetLink);
            
            logger.info("Password reset email sent to: {}", email);
            return true;
            
        } catch (Exception e) {
            logger.error("Error sending password reset email to: {}", email, e);
            return false;
        }
    }
    
    public Optional<PasswordResetToken> validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            logger.warn("Empty token provided for validation");
            return Optional.empty();
        }
        
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token.trim());
        
        if (tokenOpt.isEmpty()) {
            logger.warn("Invalid password reset token: {}", token);
            return Optional.empty();
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        
        if (resetToken.isUsed()) {
            logger.warn("Password reset token already used: {}", token);
            return Optional.empty();
        }
        
        if (resetToken.isExpired()) {
            logger.warn("Password reset token expired: {}", token);
            return Optional.empty();
        }
        
        return tokenOpt;
    }
    
    public boolean resetPassword(String token, String newPassword) {
        try {
            Optional<PasswordResetToken> tokenOpt = validateToken(token);
            if (tokenOpt.isEmpty()) {
                return false;
            }
            
            PasswordResetToken resetToken = tokenOpt.get();
            User user = resetToken.getUser();
            
            // Validate password strength
            if (newPassword == null || newPassword.trim().length() < 6) {
                logger.warn("Password too weak for user: {}", user.getEmail());
                return false;
            }
            
            // Update password
            user.setPasswordHash(PasswordUtil.hashPassword(newPassword.trim()));
            userService.saveUser(user);
            
            // Mark token as used
            resetToken.setUsed(true);
            tokenRepository.save(resetToken);
            
            logger.info("Password reset successful for user: {}", user.getEmail());
            return true;
            
        } catch (Exception e) {
            logger.error("Error resetting password for token: {}", token, e);
            return false;
        }
    }
    
    private String generateToken() {
        return UUID.randomUUID().toString();
    }
    
    private String generateResetLink(String token) {
        // For development
        if ("8080".equals(serverPort)) {
            return "http://localhost:8080" + contextPath + "/reset-password?token=" + token;
        }
        
        // For production (you can customize this based on your domain)
        return "https://mailapp-07zp.onrender.com" + contextPath + "/reset-password?token=" + token;
    }
    
    private void invalidateExistingTokens(String email) {
        try {
            // Delete expired tokens
            tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
            logger.debug("Cleaned up expired password reset tokens");
        } catch (Exception e) {
            logger.error("Error cleaning up expired tokens", e);
        }
    }
    
    public void cleanupExpiredTokens() {
        try {
            LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
            tokenRepository.deleteByExpiryDateBefore(cutoff);
            logger.info("Cleaned up expired password reset tokens");
        } catch (Exception e) {
            logger.error("Error cleaning up expired tokens", e);
        }
    }
    
    // Method to get token expiry time for display
    public int getTokenExpiryHours() {
        return TOKEN_EXPIRY_HOURS;
    }
} 