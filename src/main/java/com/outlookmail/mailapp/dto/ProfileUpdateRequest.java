package com.outlookmail.mailapp.dto;

import jakarta.validation.constraints.Size;

public class ProfileUpdateRequest {
    
    @Size(max = 100, message = "Tên hiển thị không được vượt quá 100 ký tự")
    private String displayName;
    
    @Size(max = 50, message = "Username không được vượt quá 50 ký tự")
    private String username;
    
    @Size(max = 500, message = "URL avatar không được vượt quá 500 ký tự")
    private String avatarUrl;

    public ProfileUpdateRequest() {}

    public ProfileUpdateRequest(String displayName, String username, String avatarUrl) {
        this.displayName = displayName;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "ProfileUpdateRequest{" +
                "displayName='" + displayName + '\'' +
                ", username='" + username + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
} 