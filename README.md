# MailApp - Spring Boot Application

## Vấn đề hiện tại
Hệ thống không tìm thấy email ChatGPT trong database mặc dù đã có dữ liệu.

## Các bước debug

### 1. Kiểm tra database connection
Truy cập: `https://mailapp-07zp.onrender.com/admin/debug/test-db`

### 2. Xem tất cả email ChatGPT trong database
Truy cập: `https://mailapp-07zp.onrender.com/admin/debug/chatgpt-accounts`

### 3. Tạo tài khoản test
Truy cập: `https://mailapp-07zp.onrender.com/admin/debug/create-test-account`

## Các thay đổi đã thực hiện

### 1. Sửa cấu hình database
- Loại bỏ cấu hình trùng lặp trong `application.properties`
- Thêm cấu hình connection pool cho Render.com
- Tối ưu memory settings

### 2. Thêm logging
- Thêm logging chi tiết trong `GetOtpController`
- Thêm logging trong `ChatGptAccountService`
- Log tất cả email có sẵn khi tìm kiếm thất bại

### 3. Tối ưu cho Render.com
- Cập nhật Dockerfile với JVM options
- Giảm memory usage
- Thêm connection pool settings

## Cách deploy

### 1. Build locally
```bash
chmod +x build.sh
./build.sh
```

### 2. Deploy to Render.com
- Push code lên GitHub
- Connect repository với Render.com
- Deploy tự động

## Kiểm tra logs
- Xem logs trong Render.com dashboard
- Kiểm tra database connection
- Verify data exists in PostgreSQL

## Troubleshooting

### Nếu vẫn không tìm thấy email:
1. Kiểm tra database connection
2. Verify data exists in `chatgpt_accounts` table
3. Check case sensitivity của email
4. Verify table schema matches entity

### Nếu có lỗi memory:
1. Restart service trên Render.com
2. Check memory usage
3. Optimize JVM settings

## Database Schema
```sql
CREATE TABLE chatgpt_accounts (
    id BIGSERIAL PRIMARY KEY,
    chatgpt_email VARCHAR(255) UNIQUE NOT NULL,
    secret_key VARCHAR(255) NOT NULL
);
``` 