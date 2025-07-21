package com.outlookmail.mailapp.controller;

import com.outlookmail.mailapp.model.PasswordResetToken;
import com.outlookmail.mailapp.service.PasswordResetService;
import com.outlookmail.mailapp.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PasswordResetController {
    
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);
    
    private final PasswordResetService passwordResetService;
    private final EmailService emailService;
    
    @Autowired
    public PasswordResetController(PasswordResetService passwordResetService, EmailService emailService) {
        this.passwordResetService = passwordResetService;
        this.emailService = emailService;
    }
    
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot_password";
    }
    
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập email của bạn.");
            return "forgot_password";
        }
        
        try {
            boolean emailSent = passwordResetService.sendPasswordResetEmail(email.trim());
            
            if (emailSent) {
                model.addAttribute("success", 
                    "Email đặt lại mật khẩu đã được gửi đến " + email + 
                    ". Vui lòng kiểm tra hộp thư của bạn và click vào link để đặt lại mật khẩu.");
            } else {
                model.addAttribute("error", 
                    "Không tìm thấy tài khoản với email " + email + 
                    ". Vui lòng kiểm tra lại email hoặc đăng ký tài khoản mới.");
            }
            
        } catch (Exception e) {
            logger.error("Error processing forgot password request for email: {}", email, e);
            model.addAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại sau.");
        }
        
        return "forgot_password";
    }
    
    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam String token, Model model) {
        if (token == null || token.trim().isEmpty()) {
            model.addAttribute("error", "Link đặt lại mật khẩu không hợp lệ.");
            return "reset_password";
        }
        
        try {
            var tokenOpt = passwordResetService.validateToken(token.trim());
            
            if (tokenOpt.isEmpty()) {
                model.addAttribute("error", 
                    "Link đặt lại mật khẩu không hợp lệ hoặc đã hết hạn. " +
                    "Vui lòng yêu cầu link mới.");
                return "reset_password";
            }
            
            // Token is valid, show reset form
            model.addAttribute("token", token);
            model.addAttribute("validToken", true);
            
        } catch (Exception e) {
            logger.error("Error validating reset token: {}", token, e);
            model.addAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại sau.");
        }
        
        return "reset_password";
    }
    
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                     @RequestParam String password,
                                     @RequestParam String confirmPassword,
                                     Model model) {
        
        if (token == null || token.trim().isEmpty()) {
            model.addAttribute("error", "Link đặt lại mật khẩu không hợp lệ.");
            return "reset_password";
        }
        
        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập mật khẩu mới.");
            model.addAttribute("token", token);
            model.addAttribute("validToken", true);
            return "reset_password";
        }
        
        if (confirmPassword == null || !password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp.");
            model.addAttribute("token", token);
            model.addAttribute("validToken", true);
            return "reset_password";
        }
        
        if (password.length() < 6) {
            model.addAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự.");
            model.addAttribute("token", token);
            model.addAttribute("validToken", true);
            return "reset_password";
        }
        
        try {
            boolean passwordReset = passwordResetService.resetPassword(token.trim(), password);
            
            if (passwordReset) {
                model.addAttribute("success", 
                    "Mật khẩu đã được đặt lại thành công! Bạn có thể đăng nhập với mật khẩu mới.");
                return "login";
            } else {
                model.addAttribute("error", 
                    "Không thể đặt lại mật khẩu. Link có thể đã hết hạn hoặc không hợp lệ.");
                return "reset_password";
            }
            
        } catch (Exception e) {
            logger.error("Error resetting password for token: {}", token, e);
            model.addAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại sau.");
            model.addAttribute("token", token);
            model.addAttribute("validToken", true);
            return "reset_password";
        }
    }
    
    // Debug endpoint to test email configuration
    @GetMapping("/debug/test-email")
    @ResponseBody
    public String testEmailConfiguration() {
        try {
            boolean success = emailService.testEmailConfiguration();
            if (success) {
                return "Email configuration test successful!";
            } else {
                return "Email configuration test failed. Check logs for details.";
            }
        } catch (Exception e) {
            logger.error("Error testing email configuration", e);
            return "Email configuration test failed: " + e.getMessage();
        }
    }
} 