package com.outlookmail.mailapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.outlookmail.mailapp.model.ChatGptAccount;

@Repository
public interface ChatGptAccountRepository extends JpaRepository<ChatGptAccount, Long> {
    Optional<ChatGptAccount> findByChatgptEmail(String chatgptEmail);
    boolean existsByChatgptEmail(String chatgptEmail);
    Optional<ChatGptAccount> findByChatgptEmailIgnoreCase(String chatgptEmail);
} 