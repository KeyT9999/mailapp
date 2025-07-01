package com.outlookmail.mailapp.service;

import com.outlookmail.mailapp.model.OtpRequest;
import com.outlookmail.mailapp.model.User;
import com.outlookmail.mailapp.repository.OtpRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.outlookmail.mailapp.model.UserOtpInfo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OtpRequestService {
    
    private final OtpRequestRepository otpRequestRepository;
    
    @Autowired
    public OtpRequestService(OtpRequestRepository otpRequestRepository) {
        this.otpRequestRepository = otpRequestRepository;
    }
    
    public void recordOtpRequest(User user, String ipAddress, String userAgent) {
        OtpRequest otpRequest = new OtpRequest(user, LocalDateTime.now(), ipAddress, userAgent);
        otpRequestRepository.save(otpRequest);
    }
    
    public long getOtpRequestCount(User user) {
        return otpRequestRepository.countByUser(user);
    }
    
    public List<OtpRequest> getOtpRequestsByUser(User user) {
        return otpRequestRepository.findByUserOrderByRequestTimeDesc(user);
    }
    
    public Map<User, Long> getAllUsersOtpRequestCounts() {
        List<Object[]> results = otpRequestRepository.countRequestsByUser();
        if (results == null || results.isEmpty()) {
            return new HashMap<>();
        }
        return results.stream()
                .filter(result -> result[0] != null && result[1] != null)
                .collect(Collectors.toMap(
                    result -> (User) result[0],
                    result -> ((Number) result[1]).longValue()
                ));
    }
    
    public List<UserOtpInfo> getAllUsersOtpInfo() {
        List<Object[]> results = otpRequestRepository.countRequestsByUser();
        if (results == null || results.isEmpty()) {
            return new ArrayList<>();
        }
        return results.stream()
                .filter(result -> result[0] != null && result[1] != null)
                .map(result -> new UserOtpInfo(
                    (User) result[0],
                    ((Number) result[1]).longValue(),
                    (LocalDateTime) result[2]
                ))
                .collect(Collectors.toList());
    }
    
    public long getOtpRequestCountForUser(User user) {
        Object[] result = otpRequestRepository.countRequestsBySpecificUser(user);
        return result != null ? ((Number) result[1]).longValue() : 0;
    }
} 