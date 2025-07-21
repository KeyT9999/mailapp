package com.outlookmail.mailapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender mailSender;
    
    @Value("${app.email.from:Tiem Tap Hoa KeyT <tiemtaphoakeyt@gmail.com>}")
    private String fromEmail;
    
    @Value("${app.email.reply-to:tiemtaphoakeyt@gmail.com}")
    private String replyToEmail;
    
    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    public void sendPasswordResetEmail(String toEmail, String username, String resetLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setReplyTo(replyToEmail);
            message.setTo(toEmail);
            message.setSubject("Đặt lại mật khẩu - Tiệm Tạp Hóa KeyT");
            String emailContent = createPasswordResetEmailContent(username, resetLink);
            message.setText(emailContent);
            
            mailSender.send(message);
            logger.info("Password reset email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            logger.error("Failed to send password reset email to: {}", toEmail, e);
            // In production, you might want to queue the email for retry
            // or use a fallback email service
        }
    }
    
    private String createPasswordResetEmailContent(String username, String resetLink) {
        return String.format(
            "Xin chào bạn iu %s,\n\n" +
            "Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản tại Tiệm Tạp Hóa KeyT.\n\n" +
            "Để đặt lại mật khẩu, vui lòng click vào link bên dưới:\n\n" +
            "🔗 LINK ĐẶT LẠI MẬT KHẨU:\n" +
            "%s\n\n" +
            "⚠️  Lưu ý quan trọng:\n" +
            "• Link này sẽ hết hạn sau 1 giờ\n" +
            "• Link chỉ có thể sử dụng 1 lần\n" +
            "• Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này\n\n" +
            "🔒 Bảo mật:\n" +
            "• Không chia sẻ link này với bất kỳ ai\n" +
            "• Đảm bảo bạn đang sử dụng thiết bị an toàn\n" +
            "• Sau khi đặt lại mật khẩu, hãy đăng xuất khỏi tất cả thiết bị khác\n\n" +
            "📞 Hỗ trợ:\n" +
            "Nếu bạn gặp vấn đề, vui lòng liên hệ:\n" +
            "• Zalo: 0868899104\n" +
            "• Email: support@keyt.com\n\n" +
            "Trân trọng,\n" +
            "🎯 Đội ngũ Tiệm Tạp Hóa KeyT\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "📧 Email này được gửi tự động, vui lòng không trả lời email này.\n" +
            "🔗 Website: https://mailapp-07zp.onrender.com\n" +
            "⏰ Thời gian gửi: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            username, resetLink
        );
    }
    
    public void sendWelcomeEmail(String toEmail, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setReplyTo(replyToEmail);
            message.setTo(toEmail);
            message.setSubject("Chào mừng bạn đến với Tiệm Tạp Hóa KeyT");
            String emailContent = createWelcomeEmailContent(username);
            message.setText(emailContent);
            
            mailSender.send(message);
            logger.info("Welcome email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            logger.error("Failed to send welcome email to: {}", toEmail, e);
        }
    }
    
    private String createWelcomeEmailContent(String username) {
        return String.format(
            "🎉 Xin chào %s,\n\n" +
            "Chào mừng bạn đến với Tiệm Tạp Hóa KeyT!\n\n" +
            "✅ Tài khoản của bạn đã được tạo thành công.\n" +
            "🚀 Bạn có thể đăng nhập và bắt đầu sử dụng dịch vụ ngay bây giờ.\n\n" +
            "🎯 Các dịch vụ chính:\n" +
            "• Netflix Premium - 89K/tháng\n" +
            "• Canva Pro - 189K/năm\n" +
            "• Capcut Pro - 750K/năm\n" +
            "• Vieon VIP - 49K/tháng\n" +
            "• Google 2TB - 299K/năm\n" +
            "• Spotify Premium - 365K/năm\n\n" +
            "🔒 Bảo mật tài khoản:\n" +
            "• Sử dụng mật khẩu mạnh\n" +
            "• Không chia sẻ thông tin đăng nhập\n" +
            "• Đăng xuất sau khi sử dụng xong\n\n" +
            "📞 Hỗ trợ khách hàng:\n" +
            "• Zalo: 0868899104\n" +
            "• Email: support@keyt.com\n" +
            "• Thời gian hỗ trợ: 24/7\n\n" +
            "Trân trọng,\n" +
            "🎯 Đội ngũ Tiệm Tạp Hóa KeyT\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "📧 Email này được gửi tự động, vui lòng không trả lời email này.\n" +
            "🔗 Website: https://mailapp-07zp.onrender.com\n" +
            "⏰ Thời gian gửi: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            username
        );
    }
    
    // Test method to verify email configuration
    public boolean testEmailConfiguration() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo("test@example.com");
            message.setSubject("Test Email Configuration");
            message.setText("This is a test email to verify email configuration.");
            
            mailSender.send(message);
            logger.info("Email configuration test successful");
            return true;
        } catch (Exception e) {
            logger.error("Email configuration test failed", e);
            return false;
        }
    }
} 