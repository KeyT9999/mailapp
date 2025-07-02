package com.outlookmail.mailapp.service;

import com.outlookmail.mailapp.model.User;
import com.outlookmail.mailapp.model.UserLoginHistory;
import com.outlookmail.mailapp.repository.UserLoginHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserLoginHistoryService {
    private final UserLoginHistoryRepository userLoginHistoryRepository;

    @Autowired
    public UserLoginHistoryService(UserLoginHistoryRepository userLoginHistoryRepository) {
        this.userLoginHistoryRepository = userLoginHistoryRepository;
    }

    public void recordLogin(User user, String ipAddress, String userAgent) {
        if (user == null || ipAddress == null || ipAddress.isBlank()) return;
        UserLoginHistory history = new UserLoginHistory();
        history.setUser(user);
        history.setIpAddress(ipAddress);
        history.setLoginTime(LocalDateTime.now());
        history.setUserAgent(userAgent);
        userLoginHistoryRepository.save(history);
    }

    public void recordLogin(User user, String ipAddress) {
        recordLogin(user, ipAddress, null);
    }
} 