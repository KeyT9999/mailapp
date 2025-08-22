package com.outlookmail.mailapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.outlookmail.mailapp.dto.PasswordChangeRequest;
import com.outlookmail.mailapp.dto.ProfileUpdateRequest;
import com.outlookmail.mailapp.model.User;
import com.outlookmail.mailapp.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/account")
public class AccountController {
    
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public AccountController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @GetMapping
    public String account() {
        return "redirect:/account/settings";
    }
    
    @GetMapping("/settings")
    public String showAccountSettings(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        // Refresh user data from database
        User currentUser = userService.findByEmail(user.getEmail()).orElse(user);
        session.setAttribute("user", currentUser); // Update session with fresh data
        
        model.addAttribute("user", currentUser);
        model.addAttribute("profileUpdateRequest", new ProfileUpdateRequest());
        model.addAttribute("passwordChangeRequest", new PasswordChangeRequest());
        
        return "account/settings";
    }
    
    @PostMapping("/profile")
    public String updateProfile(@RequestParam(required = false) String displayName,
                               @RequestParam(required = false) String username,
                               @RequestParam(required = false) String avatarUrl,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            // Create request object
            ProfileUpdateRequest request = new ProfileUpdateRequest(displayName, username, avatarUrl);
            
            // Validate input lengths
            if (displayName != null && displayName.length() > 100) {
                redirectAttributes.addFlashAttribute("profileError", "Tên hiển thị không được vượt quá 100 ký tự.");
                return "redirect:/account/settings";
            }
            if (username != null && username.length() > 50) {
                redirectAttributes.addFlashAttribute("profileError", "Username không được vượt quá 50 ký tự.");
                return "redirect:/account/settings";
            }
            if (avatarUrl != null && avatarUrl.length() > 500) {
                redirectAttributes.addFlashAttribute("profileError", "URL avatar không được vượt quá 500 ký tự.");
                return "redirect:/account/settings";
            }
            
            User updatedUser = userService.updateProfile(user, request);
            session.setAttribute("user", updatedUser); // Update session
            
            redirectAttributes.addFlashAttribute("profileSuccess", "Cập nhật hồ sơ thành công!");
            logger.info("User {} updated profile successfully", user.getEmail());
            
        } catch (Exception e) {
            logger.error("Error updating profile for user: {}", user.getEmail(), e);
            redirectAttributes.addFlashAttribute("profileError", "Có lỗi xảy ra khi cập nhật hồ sơ: " + e.getMessage());
        }
        
        return "redirect:/account/settings";
    }
    
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        // Basic validation
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("passwordError", "Vui lòng nhập mật khẩu hiện tại.");
            return "redirect:/account/settings";
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("passwordError", "Vui lòng nhập mật khẩu mới.");
            return "redirect:/account/settings";
        }
        
        if (newPassword.length() < 8) {
            redirectAttributes.addFlashAttribute("passwordError", "Mật khẩu mới phải có ít nhất 8 ký tự.");
            return "redirect:/account/settings";
        }
        
        if (!newPassword.matches("^(?=.*[a-zA-Z])(?=.*\\d).*$")) {
            redirectAttributes.addFlashAttribute("passwordError", "Mật khẩu mới phải chứa ít nhất 1 chữ cái và 1 số.");
            return "redirect:/account/settings";
        }
        
        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("passwordError", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
            return "redirect:/account/settings";
        }
        
        try {
            // Create request object
            PasswordChangeRequest request = new PasswordChangeRequest(currentPassword, newPassword, confirmPassword);
            boolean success = userService.changePassword(user, request, passwordEncoder);
            
            if (success) {
                redirectAttributes.addFlashAttribute("passwordSuccess", "Đổi mật khẩu thành công!");
                logger.info("User {} changed password successfully", user.getEmail());
            } else {
                redirectAttributes.addFlashAttribute("passwordError", "Mật khẩu hiện tại không đúng.");
            }
            
        } catch (Exception e) {
            logger.error("Error changing password for user: {}", user.getEmail(), e);
            redirectAttributes.addFlashAttribute("passwordError", "Có lỗi xảy ra khi đổi mật khẩu: " + e.getMessage());
        }
        
        return "redirect:/account/settings";
    }
} 