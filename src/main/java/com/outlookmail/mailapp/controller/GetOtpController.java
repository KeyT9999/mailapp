package com.outlookmail.mailapp.controller;

import com.outlookmail.mailapp.model.ChatGptAccount;
import com.outlookmail.mailapp.model.User;
import com.outlookmail.mailapp.service.ChatGptAccountService;
import com.outlookmail.mailapp.service.OtpRequestService;
import com.outlookmail.mailapp.service.UserService;
import com.outlookmail.mailapp.util.TOTPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class GetOtpController {
    
    private final ChatGptAccountService chatGptAccountService;
    private final OtpRequestService otpRequestService;
    private final UserService userService;
    
    @Autowired
    public GetOtpController(ChatGptAccountService chatGptAccountService, 
                          OtpRequestService otpRequestService,
                          UserService userService) {
        this.chatGptAccountService = chatGptAccountService;
        this.otpRequestService = otpRequestService;
        this.userService = userService;
    }
    
    @GetMapping("/get-otp")
    public String showGetOtpPage() {
        return "get_otp";
    }
    
    @PostMapping("/get-otp")
    public String getOtp(@RequestParam String chatgptEmail,
                        Model model,
                        HttpServletRequest request) {
        
        if (chatgptEmail == null || chatgptEmail.isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập email ChatGPT.");
            return "get_otp";
        }
        
        ChatGptAccount account = chatGptAccountService.findByEmail(chatgptEmail).orElse(null);
        if (account == null) {
            model.addAttribute("error", "Không tìm thấy email ChatGPT trong hệ thống!");
        } else {
            String otp = TOTPUtil.getTOTPCode(account.getSecretKey());
            model.addAttribute("otp", otp);
            
            // Ghi lại lần yêu cầu mã OTP
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !"anonymousUser".equals(authentication.getName())) {
                
                User user = userService.findByEmail(authentication.getName()).orElse(null);
                if (user != null) {
                    String ipAddress = getClientIpAddress(request);
                    String userAgent = request.getHeader("User-Agent");
                    otpRequestService.recordOtpRequest(user, ipAddress, userAgent);
                }
            }
        }
        
        return "get_otp";
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
} 