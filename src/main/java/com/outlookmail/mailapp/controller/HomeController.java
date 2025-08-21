package com.outlookmail.mailapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.outlookmail.mailapp.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        // Add user info to model for display
        model.addAttribute("user", user);
        
        if (user.isAdmin()) {
            return "redirect:/admin/dashboard";
        } else {
            return "home"; // Show new home page for regular users
        }
    }
    
    @GetMapping("/user/dashboard")
    public String userDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "user/dashboard";
    }
} 