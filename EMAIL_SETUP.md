# Hướng dẫn Setup Email cho Chức năng Quên Mật khẩu

## 📧 **Cấu hình Email**

### **1. Sử dụng Gmail SMTP**

#### **Bước 1: Tạo App Password**
1. Đăng nhập vào Google Account
2. Vào **Security** → **2-Step Verification** (bật nếu chưa bật)
3. Vào **Security** → **App passwords**
4. Chọn **Mail** và **Other (Custom name)**
5. Đặt tên: `Tiệm Tạp Hóa KeyT`
6. Copy App Password được tạo

#### **Bước 2: Cấu hình Environment Variables**

**Cho Development (local):**
```bash
# Windows
set MAIL_USERNAME=your-email@gmail.com
set MAIL_PASSWORD=your-app-password
set MAIL_FROM=Tiệm Tạp Hóa KeyT <your-email@gmail.com>
set MAIL_REPLY_TO=your-email@gmail.com

# Linux/Mac
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
export MAIL_FROM="Tiệm Tạp Hóa KeyT <your-email@gmail.com>"
export MAIL_REPLY_TO=your-email@gmail.com
```

**Cho Production (Render.com):**
1. Vào Render Dashboard → Your App → Environment
2. Thêm các variables:
   - `MAIL_USERNAME`: your-email@gmail.com
   - `MAIL_PASSWORD`: your-app-password
   - `MAIL_FROM`: Tiệm Tạp Hóa KeyT <your-email@gmail.com>
   - `MAIL_REPLY_TO`: your-email@gmail.com

### **2. Sử dụng Email Service khác**

#### **Outlook/Hotmail:**
```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
spring.mail.username=your-email@outlook.com
spring.mail.password=your-password
```

#### **Yahoo:**
```properties
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587
spring.mail.username=your-email@yahoo.com
spring.mail.password=your-app-password
```

## 🧪 **Test Email Configuration**

### **1. Test qua Browser:**
```
http://localhost:8080/debug/test-email
```

### **2. Test qua Logs:**
Kiểm tra logs khi gửi email reset password:
```bash
tail -f logs/application.log
```

## 🔧 **Troubleshooting**

### **Lỗi thường gặp:**

#### **1. Authentication failed**
```
Error: 535 5.7.8 Username and Password not accepted
```
**Giải pháp:**
- Kiểm tra App Password đúng chưa
- Đảm bảo 2FA đã bật
- Thử tạo App Password mới

#### **2. Connection timeout**
```
Error: Connection timed out
```
**Giải pháp:**
- Kiểm tra firewall
- Thử port 465 với SSL
- Kiểm tra network connection

#### **3. Invalid email address**
```
Error: Invalid email address
```
**Giải pháp:**
- Kiểm tra format email trong application.properties
- Đảm bảo email hợp lệ

## 📋 **Luồng hoạt động**

### **1. User quên mật khẩu:**
1. Click "Quên mật khẩu" trên trang login
2. Nhập email → Submit form
3. System tạo token và gửi email
4. User nhận email với link reset

### **2. User reset mật khẩu:**
1. Click link trong email
2. Nhập mật khẩu mới
3. System validate và update password
4. Redirect về trang login

## 🔒 **Bảo mật**

### **1. Token Security:**
- Token có thời hạn 1 giờ
- Token chỉ sử dụng 1 lần
- Token được hash và lưu an toàn

### **2. Email Security:**
- Sử dụng TLS/SSL
- Không lưu password trong code
- Sử dụng environment variables

### **3. Rate Limiting:**
- Giới hạn số lần gửi email reset
- Cleanup tokens expired
- Logging đầy đủ

## 📊 **Monitoring**

### **1. Logs quan trọng:**
```bash
# Email sent successfully
INFO - Password reset email sent to: user@example.com

# Email failed
ERROR - Failed to send password reset email to: user@example.com

# Token validation
WARN - Password reset token expired: token123
```

### **2. Database queries:**
```sql
-- Check active tokens
SELECT * FROM password_reset_tokens WHERE used = false AND expiry_date > NOW();

-- Cleanup expired tokens
DELETE FROM password_reset_tokens WHERE expiry_date < NOW();
```

## 🚀 **Deployment Checklist**

- [ ] Cấu hình environment variables
- [ ] Test email configuration
- [ ] Verify SMTP settings
- [ ] Check firewall rules
- [ ] Monitor email delivery
- [ ] Setup error alerts
- [ ] Backup email configuration

## 📞 **Support**

Nếu gặp vấn đề:
1. Kiểm tra logs trước
2. Test email configuration
3. Verify environment variables
4. Contact admin nếu cần 