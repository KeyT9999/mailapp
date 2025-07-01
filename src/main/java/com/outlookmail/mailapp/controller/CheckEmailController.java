package com.outlookmail.mailapp.controller;

import com.outlookmail.mailapp.model.User;
import com.outlookmail.mailapp.service.UserService;
import com.outlookmail.mailapp.util.TOTPUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CheckEmailController {
    
    private final UserService userService;
    
    @Autowired
    public CheckEmailController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/check-email")
    public String showCheckEmailPage() {
        return "check_email";
    }
    
    @PostMapping("/check-email")
    public String checkEmail(@RequestParam String email,
                            HttpSession session,
                            Model model) {
        
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            model.addAttribute("error", "Email không tồn tại trong hệ thống.");
            return "check_email";
        }
        
        String code = TOTPUtil.getTOTPCode(user.getSecretKey());
        
        // Lưu email vào session để xác thực bước sau
        session.setAttribute("2fa_email", email);
        
        // Hiển thị mã 2FA trực tiếp trên giao diện
        model.addAttribute("success", "Mã 2FA của bạn là: <b>" + code + "</b>");
        return "verify_2fa";
    }
} 