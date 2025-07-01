package com.outlookmail.mailapp.repository;

import com.outlookmail.mailapp.model.ChatGptAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatGptAccountRepository extends JpaRepository<ChatGptAccount, Long> {
    Optional<ChatGptAccount> findByChatgptEmail(String chatgptEmail);
    boolean existsByChatgptEmail(String chatgptEmail);
} 