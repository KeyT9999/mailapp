package com.outlookmail.mailapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.outlookmail.mailapp.service.UserService;

@Controller
public class RegisterController {
    
    private final UserService userService;
    
    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }
    
    @PostMapping("/register")
    public String register(@RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        
        if (email == null || password == null || confirmPassword == null ||
            email.trim().isEmpty() || password.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập đầy đủ thông tin.");
            return "register";
        }
        
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu nhập lại không khớp.");
            return "register";
        }
        
        try {
            userService.createUser(email, password, "user");
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập để bắt đầu sử dụng. Nếu chưa nhận được email xác minh, hãy kiểm tra hộp thư rác.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Đăng ký thất bại: " + e.getMessage());
        }
        
        return "register";
    }
} 