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
        if (account.getChatgptEmail() != null) {
            String normalized = account.getChatgptEmail().trim().toLowerCase();
            logger.debug("Normalized email before save: '{}' -> '{}'", account.getChatgptEmail(), normalized);
            account.setChatgptEmail(normalized);
        }
        return chatGptAccountRepository.save(account);
    }
    
    public Optional<ChatGptAccount> findByEmail(String chatgptEmail) {
        if (chatgptEmail == null) return Optional.empty();
        String normalized = chatgptEmail.trim().toLowerCase();
        logger.debug("Normalized input email for search: '{}' -> '{}'", chatgptEmail, normalized);
        Optional<ChatGptAccount> result = chatGptAccountRepository.findByChatgptEmailIgnoreCase(normalized);
        if (result.isPresent()) {
            logger.info("Found ChatGPT account: {}", normalized);
        } else {
            logger.warn("ChatGPT email not found: {}", normalized);
            List<ChatGptAccount> allAccounts = chatGptAccountRepository.findAll();
            logger.info("Total ChatGPT accounts in database: {}", allAccounts.size());
            for (ChatGptAccount acc : allAccounts) {
                logger.info("Available account: {}", acc.getChatgptEmail());
            }
        }
        return result;
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
    
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public ChatGptAccount getChatGptAccountById(Long id) {
        return chatGptAccountRepository.findById(id).orElse(null);
    }
    
    public ChatGptAccount updateChatGptAccount(Long id, String chatgptEmail, String secretKey) {
        ChatGptAccount account = chatGptAccountRepository.findById(id).orElse(null);
        if (account != null) {
            if (chatgptEmail != null) {
                String normalized = chatgptEmail.trim().toLowerCase();
                account.setChatgptEmail(normalized);
            }
            if (secretKey != null) {
                account.setSecretKey(secretKey);
            }
            return chatGptAccountRepository.save(account);
        }
        return null;
    }
    
    public boolean deleteChatGptAccount(Long id) {
        if (chatGptAccountRepository.existsById(id)) {
            chatGptAccountRepository.deleteById(id);
            return true;
        }
        return false;
    }
} 