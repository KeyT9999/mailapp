package com.outlookmail.mailapp.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.outlookmail.mailapp.model.ChatGptAccount;
import com.outlookmail.mailapp.model.User;
import com.outlookmail.mailapp.model.UserOtpInfo;
import com.outlookmail.mailapp.service.ChatGptAccountService;
import com.outlookmail.mailapp.service.OtpRequestService;
import com.outlookmail.mailapp.util.TOTPUtil;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final ChatGptAccountService chatGptAccountService;
    private final OtpRequestService otpRequestService;
    
    @Autowired
    public AdminController(ChatGptAccountService chatGptAccountService, 
                         OtpRequestService otpRequestService) {
        this.chatGptAccountService = chatGptAccountService;
        this.otpRequestService = otpRequestService;
    }
    
    @GetMapping("/add-chatgpt-email")
    public String showAddChatGptEmailPage(Model model) {
        List<User> users = chatGptAccountService.getAllUsers();
        List<UserOtpInfo> userOtpInfos = otpRequestService.getAllUsersOtpInfo();
        
        // Tạo Map để dễ dàng truy cập thông tin OTP
        Map<Long, UserOtpInfo> userOtpInfoMap = userOtpInfos.stream()
                .collect(Collectors.toMap(info -> info.getUser().getId(), info -> info));
        
        model.addAttribute("users", users);
        model.addAttribute("userOtpInfoMap", userOtpInfoMap);
        return "add_chatgpt_email";
    }
    
    @PostMapping("/add-chatgpt-email")
    public String addChatGptEmail(@RequestParam String chatgptEmail,
                                 @RequestParam String secretKey,
                                 Model model) {
        
        if (chatgptEmail == null || secretKey == null || 
            chatgptEmail.isEmpty() || secretKey.isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập đầy đủ thông tin.");
            List<User> users = chatGptAccountService.getAllUsers();
            List<UserOtpInfo> userOtpInfos = otpRequestService.getAllUsersOtpInfo();
            Map<Long, UserOtpInfo> userOtpInfoMap = userOtpInfos.stream()
                    .collect(Collectors.toMap(info -> info.getUser().getId(), info -> info));
            model.addAttribute("users", users);
            model.addAttribute("userOtpInfoMap", userOtpInfoMap);
            return "add_chatgpt_email";
        }
        
        try {
            ChatGptAccount account = new ChatGptAccount(null, chatgptEmail, secretKey);
            chatGptAccountService.saveChatGptAccount(account);
            
            String otp = TOTPUtil.getTOTPCode(secretKey);
            model.addAttribute("success", "Thêm email ChatGPT thành công!<br>SecretKey: <b>" + 
                              secretKey + "</b><br>Mã 2FA hiện tại: <b style='color:#b71c1c;font-size:1.3em'>" + 
                              otp + "</b>");
        } catch (Exception e) {
            model.addAttribute("error", "Thêm email ChatGPT thất bại hoặc email đã tồn tại!");
        }
        
        List<User> users = chatGptAccountService.getAllUsers();
        List<UserOtpInfo> userOtpInfos = otpRequestService.getAllUsersOtpInfo();
        Map<Long, UserOtpInfo> userOtpInfoMap = userOtpInfos.stream()
                .collect(Collectors.toMap(info -> info.getUser().getId(), info -> info));
        model.addAttribute("users", users);
        model.addAttribute("userOtpInfoMap", userOtpInfoMap);
        return "add_chatgpt_email";
    }
    
    @GetMapping("/dashboard")
    public String showDashboard() {
        return "admin/dashboard";
    }
    
    @GetMapping("/debug/chatgpt-accounts")
    @ResponseBody
    public String debugChatGptAccounts() {
        try {
            List<ChatGptAccount> accounts = chatGptAccountService.getAllChatGptAccounts();
            StringBuilder result = new StringBuilder();
            result.append("Total ChatGPT accounts: ").append(accounts.size()).append("\n");
            for (ChatGptAccount account : accounts) {
                result.append("ID: ").append(account.getId())
                      .append(", Email: ").append(account.getChatgptEmail())
                      .append(", SecretKey: ").append(account.getSecretKey())
                      .append("\n");
            }
            return result.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    @GetMapping("/debug/test-db")
    @ResponseBody
    public String testDatabase() {
        try {
            // Test database connection
            List<ChatGptAccount> accounts = chatGptAccountService.getAllChatGptAccounts();
            return "Database connection successful. Total accounts: " + accounts.size();
        } catch (Exception e) {
            return "Database connection failed: " + e.getMessage();
        }
    }
    
    @GetMapping("/debug/create-test-account")
    @ResponseBody
    public String createTestAccount() {
        try {
            // Create a test ChatGPT account
            ChatGptAccount testAccount = new ChatGptAccount();
            testAccount.setChatgptEmail("test@example.com");
            testAccount.setSecretKey("JBSWY3DPEHPK3PXP");
            
            ChatGptAccount saved = chatGptAccountService.saveChatGptAccount(testAccount);
            return "Test account created successfully. ID: " + saved.getId();
        } catch (Exception e) {
            return "Failed to create test account: " + e.getMessage();
        }
    }
} 