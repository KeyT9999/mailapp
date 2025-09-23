package com.outlookmail.mailapp.config;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.outlookmail.mailapp.model.User;
import com.outlookmail.mailapp.repository.UserRepository;
import com.outlookmail.mailapp.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);

    private final UserRepository userRepository;

    public OAuth2LoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof OAuth2User)) {
            response.sendRedirect("/login");
            return;
        }

        OAuth2User oauth2User = (OAuth2User) principal;
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = getString(attributes, "email");
        String name = getString(attributes, "name");
        String picture = getString(attributes, "picture");

        if (email == null || email.isBlank()) {
            logger.warn("OAuth2 login succeeded but no email present in attributes: {}", attributes.keySet());
            response.sendRedirect("/login?error");
            return;
        }

        Optional<User> existingOpt = userRepository.findByEmail(email);
        User user = existingOpt.orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setDisplayName(name);
            newUser.setAvatarUrl(picture);
            newUser.setRole("user");
            // generate a random password hash as placeholder
            newUser.setPasswordHash(PasswordUtil.hashPassword(UUID.randomUUID().toString()));
            return userRepository.save(newUser);
        });

        // Update profile fields on each login
        boolean changed = false;
        if (name != null && !name.equals(user.getDisplayName())) {
            user.setDisplayName(name);
            changed = true;
        }
        if (picture != null && !picture.equals(user.getAvatarUrl())) {
            user.setAvatarUrl(picture);
            changed = true;
        }
        if (changed) {
            userRepository.save(user);
        }

        // Align with existing session-based auth
        try {
            request.getSession(false).invalidate();
        } catch (Exception ignored) {}
        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("user", user);
        newSession.setMaxInactiveInterval(30 * 60);

        // Redirect based on role like LoginController
        if (user.isAdmin()) {
            response.sendRedirect("/admin/dashboard");
        } else {
            response.sendRedirect("/get-otp");
        }
    }

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value == null ? null : String.valueOf(value);
    }
} 