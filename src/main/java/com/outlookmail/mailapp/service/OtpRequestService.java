package com.outlookmail.mailapp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.outlookmail.mailapp.model.OtpRequest;
import com.outlookmail.mailapp.model.User;
import com.outlookmail.mailapp.model.UserOtpInfo;
import com.outlookmail.mailapp.repository.OtpRequestRepository;

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
    
    /**
     * Kiểm tra và ghi nhận yêu cầu OTP, chỉ cho phép tối đa 2 IP đầu tiên.
     * @return true nếu hợp lệ và đã lưu, false nếu vượt quá 2 IP.
     */
    public boolean recordOtpRequestWithIpLimit(User user, String ipAddress, String userAgent) {
        if (ipAddress == null || ipAddress.isBlank()) return false;
        List<String> first2Ips = otpRequestRepository.findFirst2DistinctIpByUser(user);
        if (first2Ips.size() < 2 || first2Ips.contains(ipAddress)) {
            // Cho phép lưu, không tăng số IP nếu trùng
            recordOtpRequest(user, ipAddress, userAgent);
            return true;
        } else {
            // Đã có 2 IP khác, không cho phép thêm IP mới
            return false;
        }
    }
} 