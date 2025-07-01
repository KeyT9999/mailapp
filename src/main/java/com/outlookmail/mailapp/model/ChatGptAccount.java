package com.outlookmail.mailapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "chatgpt_accounts")
public class ChatGptAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Email
    @Column(name = "chatgpt_email", unique = true, nullable = false)
    private String chatgptEmail;
    
    @NotBlank
    @Column(name = "secret_key", nullable = false)
    private String secretKey;

    public ChatGptAccount() {}

    public ChatGptAccount(Long id, String chatgptEmail, String secretKey) {
        this.id = id;
        this.chatgptEmail = chatgptEmail;
        this.secretKey = secretKey;
    }

    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public String getChatgptEmail() { 
        return chatgptEmail; 
    }
    
    public void setChatgptEmail(String chatgptEmail) { 
        this.chatgptEmail = chatgptEmail; 
    }
    
    public String getSecretKey() { 
        return secretKey; 
    }
    
    public void setSecretKey(String secretKey) { 
        this.secretKey = secretKey; 
    }
} 