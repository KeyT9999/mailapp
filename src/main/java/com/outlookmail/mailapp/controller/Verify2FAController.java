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
public class Verify2FAController {
    
    private final UserService userService;
    
    @Autowired
    public Verify2FAController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/verify-2fa")
    public String verify2FA(@RequestParam String code,
                           HttpSession session,
                           Model model) {
        
        String email = (String) session.getAttribute("2fa_email");
        if (email == null) {
            model.addAttribute("error", "Phiên xác thực không hợp lệ.");
            return "verify_2fa";
        }
        
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            model.addAttribute("error", "Email không tồn tại.");
            return "verify_2fa";
        }
        
        String validCode = TOTPUtil.getTOTPCode(user.getSecretKey());
        if (code != null && code.equals(validCode)) {
            model.addAttribute("success", "Xác thực 2FA thành công!");
            session.removeAttribute("2fa_email");
        } else {
            model.addAttribute("error", "Mã 2FA không đúng hoặc đã hết hạn.");
        }
        
        return "verify_2fa";
    }
} 