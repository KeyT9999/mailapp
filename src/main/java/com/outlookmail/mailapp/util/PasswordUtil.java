package com.outlookmail.mailapp.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    // Băm mật khẩu
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Kiểm tra mật khẩu với hash
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    // Test nhanh
    public static void main(String[] args) {
        String password = "123456";
        String hash = hashPassword(password);
        System.out.println("Hash: " + hash);
        System.out.println("Check đúng: " + checkPassword("123456", hash));
        System.out.println("Check sai: " + checkPassword("wrongpass", hash));
    }
} 