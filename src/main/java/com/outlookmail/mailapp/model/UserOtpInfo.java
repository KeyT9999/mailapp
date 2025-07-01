package com.outlookmail.mailapp.model;

import java.time.LocalDateTime;

public class UserOtpInfo {
    private User user;
    private long requestCount;
    private LocalDateTime lastRequestTime;
    
    public UserOtpInfo(User user, long requestCount, LocalDateTime lastRequestTime) {
        this.user = user;
        this.requestCount = requestCount;
        this.lastRequestTime = lastRequestTime;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public long getRequestCount() {
        return requestCount;
    }
    
    public void setRequestCount(long requestCount) {
        this.requestCount = requestCount;
    }
    
    public LocalDateTime getLastRequestTime() {
        return lastRequestTime;
    }
    
    public void setLastRequestTime(LocalDateTime lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }
    
    public String getFormattedLastRequestTime() {
        if (lastRequestTime == null) {
            return "Chưa có";
        }
        return lastRequestTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
} 