package com.outlookmail.mailapp.util;

import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;

public class TOTPUtil {
    // Tạo secret mới cho user (dùng khi bật 2FA)
    public static String generateSecret() {
        return Base32.random();
    }

    // Sinh mã TOTP 6 số từ secret
    public static String getTOTPCode(String secret) {
        Totp totp = new Totp(secret);
        return totp.now();
    }
} 