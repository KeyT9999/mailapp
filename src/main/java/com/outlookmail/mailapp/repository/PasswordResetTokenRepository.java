package com.outlookmail.mailapp.repository;

import com.outlookmail.mailapp.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    Optional<PasswordResetToken> findByToken(String token);
    
    List<PasswordResetToken> findByUserEmail(String email);
    
    List<PasswordResetToken> findByExpiryDateBefore(LocalDateTime date);
    
    void deleteByExpiryDateBefore(LocalDateTime date);
} 