package com.outlookmail.mailapp.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outlookmail.mailapp.model.ChatGptAccount;
import com.outlookmail.mailapp.model.User;
import com.outlookmail.mailapp.repository.ChatGptAccountRepository;
import com.outlookmail.mailapp.repository.UserRepository;

@Service
@Transactional
public class ChatGptAccountService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatGptAccountService.class);
    
    private final ChatGptAccountRepository chatGptAccountRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public ChatGptAccountService(ChatGptAccountRepository chatGptAccountRepository, 
                                UserRepository userRepository) {
        this.chatGptAccountRepository = chatGptAccountRepository;
        this.userRepository = userRepository;
    }
    
    public ChatGptAccount saveChatGptAccount(ChatGptAccount account) {
        logger.info("Saving ChatGPT account: {}", account.getChatgptEmail());
        return chatGptAccountRepository.save(account);
    }
    
    public Optional<ChatGptAccount> findByEmail(String chatgptEmail) {
        logger.info("Searching for ChatGPT email in database: {}", chatgptEmail);
        try {
            Optional<ChatGptAccount> result = chatGptAccountRepository.findByChatgptEmail(chatgptEmail);
            if (result.isPresent()) {
                logger.info("Found ChatGPT account: {}", chatgptEmail);
            } else {
                logger.warn("ChatGPT email not found: {}", chatgptEmail);
                // Log all available emails for debugging
                List<ChatGptAccount> allAccounts = chatGptAccountRepository.findAll();
                logger.info("Total ChatGPT accounts in database: {}", allAccounts.size());
                for (ChatGptAccount acc : allAccounts) {
                    logger.info("Available account: {}", acc.getChatgptEmail());
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("Error searching for ChatGPT email: {}", chatgptEmail, e);
            throw e;
        }
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public boolean existsByEmail(String chatgptEmail) {
        logger.info("Checking if ChatGPT email exists: {}", chatgptEmail);
        return chatGptAccountRepository.existsByChatgptEmail(chatgptEmail);
    }
    
    public List<ChatGptAccount> getAllChatGptAccounts() {
        logger.info("Getting all ChatGPT accounts");
        return chatGptAccountRepository.findAll();
    }
} 