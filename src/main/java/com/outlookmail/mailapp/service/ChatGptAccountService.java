package com.outlookmail.mailapp.service;

import com.outlookmail.mailapp.model.ChatGptAccount;
import com.outlookmail.mailapp.model.User;
import com.outlookmail.mailapp.repository.ChatGptAccountRepository;
import com.outlookmail.mailapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChatGptAccountService {
    
    private final ChatGptAccountRepository chatGptAccountRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public ChatGptAccountService(ChatGptAccountRepository chatGptAccountRepository, 
                                UserRepository userRepository) {
        this.chatGptAccountRepository = chatGptAccountRepository;
        this.userRepository = userRepository;
    }
    
    public ChatGptAccount saveChatGptAccount(ChatGptAccount account) {
        return chatGptAccountRepository.save(account);
    }
    
    public Optional<ChatGptAccount> findByEmail(String chatgptEmail) {
        return chatGptAccountRepository.findByChatgptEmail(chatgptEmail);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public boolean existsByEmail(String chatgptEmail) {
        return chatGptAccountRepository.existsByChatgptEmail(chatgptEmail);
    }
} 