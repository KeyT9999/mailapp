package com.outlookmail.mailapp.controller;

import com.outlookmail.mailapp.model.User;
import com.outlookmail.mailapp.service.UserService;
import com.outlookmail.mailapp.service.UserLoginHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    private final UserService userService;
    private final UserLoginHistoryService userLoginHistoryService;
    
    @Autowired
    public LoginController(UserService userService, UserLoginHistoryService userLoginHistoryService) {
        this.userService = userService;
        this.userLoginHistoryService = userLoginHistoryService;
    }
    
    @GetMapping("/login")
    public String showLoginPage(HttpSession session, Model model) {
        // Check if user is already logged in
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return redirectBasedOnRole(user);
        }
        
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String email, 
                       @RequestParam String password,
                       HttpServletRequest request,
                       HttpSession session,
                       Model model) {
        if (email == null || password == null || 
            email.trim().isEmpty() || password.trim().isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập đầy đủ thông tin.");
            return "login";
        }
        if (userService.authenticate(email, password)) {
            User user = userService.findByEmail(email).orElse(null);
            if (user != null) {
                // Invalidate existing session
                try {
                    session.invalidate();
                } catch (IllegalStateException ignored) {}
                // Create new session
                HttpSession newSession = request.getSession(true);
                newSession.setAttribute("user", user);
                newSession.setMaxInactiveInterval(30 * 60); // 30 minutes
                logger.info("User {} logged in successfully", email);
                // Ghi nhận lịch sử IP đăng nhập
                String ipAddress = getClientIpAddress(request);
                String userAgent = request.getHeader("User-Agent");
                userLoginHistoryService.recordLogin(user, ipAddress, userAgent);
                if (user.isAdmin()) {
                    return "redirect:/admin/add-chatgpt-email";
                } else {
                    return "redirect:/get-otp";
                }
            }
        }
        model.addAttribute("error", "Email hoặc mật khẩu không đúng.");
        return "login";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            logger.info("User {} logged out", user.getEmail());
        }
        session.invalidate();
        return "redirect:/login";
    }
    
    private String redirectBasedOnRole(User user) {
        if (user.isAdmin()) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/user/dashboard";
        }
    }
    
    // Thêm hàm lấy IP client
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