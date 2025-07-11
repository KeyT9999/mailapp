package com.outlookmail.mailapp.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
import com.outlookmail.mailapp.repository.OtpRequestRepository;
import com.outlookmail.mailapp.repository.UserLoginHistoryRepository;
import com.outlookmail.mailapp.model.UserLoginHistory;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final ChatGptAccountService chatGptAccountService;
    private final OtpRequestService otpRequestService;
    private final OtpRequestRepository otpRequestRepository;
    private final UserLoginHistoryRepository userLoginHistoryRepository;
    
    @Autowired
    public AdminController(ChatGptAccountService chatGptAccountService, 
                         OtpRequestService otpRequestService,
                         OtpRequestRepository otpRequestRepository,
                         UserLoginHistoryRepository userLoginHistoryRepository) {
        this.chatGptAccountService = chatGptAccountService;
        this.otpRequestService = otpRequestService;
        this.otpRequestRepository = otpRequestRepository;
        this.userLoginHistoryRepository = userLoginHistoryRepository;
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
    
    @GetMapping("/user-login-history")
    public String showUserLoginHistory(@RequestParam Long userId, Model model) {
        User user = chatGptAccountService.getUserById(userId);
        List<UserLoginHistory> history = userLoginHistoryRepository.findByUserOrderByLoginTimeDesc(user);
        List<UserLoginHistory> historyAsc = userLoginHistoryRepository.findByUserOrderByLoginTimeAsc(user);
        List<String> first2Ips = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (UserLoginHistory h : historyAsc) {
            if (h.getIpAddress() != null && seen.add(h.getIpAddress())) {
                first2Ips.add(h.getIpAddress());
                if (first2Ips.size() == 2) break;
            }
        }
        long distinctIpCount = userLoginHistoryRepository.countDistinctIpByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("history", history);
        model.addAttribute("first2Ips", first2Ips);
        model.addAttribute("distinctIpCount", distinctIpCount);
        return "admin/user_login_history";
    }
    
    @GetMapping("/chatgpt-accounts")
    public String showChatGptAccounts(Model model) {
        List<ChatGptAccount> accounts = chatGptAccountService.getAllChatGptAccounts();
        model.addAttribute("accounts", accounts);
        return "admin/chatgpt_accounts";
    }
    
    @GetMapping("/edit-chatgpt-account")
    public String showEditChatGptAccount(@RequestParam Long id, Model model) {
        ChatGptAccount account = chatGptAccountService.getChatGptAccountById(id);
        if (account == null) {
            model.addAttribute("error", "Không tìm thấy tài khoản ChatGPT với ID: " + id);
            return "redirect:/admin/chatgpt-accounts";
        }
        model.addAttribute("account", account);
        return "admin/edit_chatgpt_account";
    }
    
    @PostMapping("/update-chatgpt-account")
    public String updateChatGptAccount(@RequestParam Long id,
                                     @RequestParam String chatgptEmail,
                                     @RequestParam String secretKey,
                                     Model model) {
        
        if (chatgptEmail == null || secretKey == null || 
            chatgptEmail.isEmpty() || secretKey.isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập đầy đủ thông tin.");
            ChatGptAccount account = chatGptAccountService.getChatGptAccountById(id);
            model.addAttribute("account", account);
            return "admin/edit_chatgpt_account";
        }
        
        try {
            ChatGptAccount updatedAccount = chatGptAccountService.updateChatGptAccount(id, chatgptEmail, secretKey);
            if (updatedAccount != null) {
                String otp = TOTPUtil.getTOTPCode(secretKey);
                model.addAttribute("success", "Cập nhật email ChatGPT thành công!<br>SecretKey: <b>" + 
                                  secretKey + "</b><br>Mã 2FA hiện tại: <b style='color:#b71c1c;font-size:1.3em'>" + 
                                  otp + "</b>");
            } else {
                model.addAttribute("error", "Không tìm thấy tài khoản để cập nhật!");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Cập nhật email ChatGPT thất bại: " + e.getMessage());
        }
        
        ChatGptAccount account = chatGptAccountService.getChatGptAccountById(id);
        model.addAttribute("account", account);
        return "admin/edit_chatgpt_account";
    }
    
    @PostMapping("/delete-chatgpt-account")
    public String deleteChatGptAccount(@RequestParam Long id, Model model) {
        try {
            boolean deleted = chatGptAccountService.deleteChatGptAccount(id);
            if (deleted) {
                model.addAttribute("success", "Xóa email ChatGPT thành công!");
            } else {
                model.addAttribute("error", "Không tìm thấy tài khoản để xóa!");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Xóa email ChatGPT thất bại: " + e.getMessage());
        }
        
        List<ChatGptAccount> accounts = chatGptAccountService.getAllChatGptAccounts();
        model.addAttribute("accounts", accounts);
        return "admin/chatgpt_accounts";
    }
} 