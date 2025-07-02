# Hướng dẫn khắc phục sự cố

## Vấn đề: Không tìm thấy email ChatGPT trong hệ thống

### Nguyên nhân có thể:

1. **Database Connection Issues**
   - Kết nối database bị gián đoạn
   - Credentials không đúng
   - Database server down

2. **Data Issues**
   - Email không tồn tại trong database
   - Case sensitivity (chữ hoa/chữ thường)
   - Whitespace trong email

3. **Application Issues**
   - Memory problems
   - Service restart
   - Cache issues

### Các bước debug:

#### 1. Kiểm tra Database Connection
```
https://mailapp-07zp.onrender.com/admin/debug/test-db
```

#### 2. Xem tất cả email trong database
```
https://mailapp-07zp.onrender.com/admin/debug/chatgpt-accounts
```

#### 3. Tạo tài khoản test
```
https://mailapp-07zp.onrender.com/admin/debug/create-test-account
```

### Cách khắc phục:

#### Nếu database connection failed:
1. Kiểm tra credentials trong `application.properties`
2. Verify database server status
3. Check network connectivity

#### Nếu email không tồn tại:
1. Thêm email vào database qua admin panel
2. Kiểm tra case sensitivity
3. Verify email format

#### Nếu có lỗi memory:
1. Restart service trên Render.com
2. Check memory usage
3. Optimize JVM settings

### Logs quan trọng:

Kiểm tra logs trong Render.com dashboard để xem:
- Database connection errors
- SQL query logs
- Memory usage
- Application startup logs

### Test cases:

1. **Test với email có sẵn:**
   - Đăng nhập admin
   - Thêm email ChatGPT
   - Test tìm kiếm email đó

2. **Test với email không tồn tại:**
   - Nhập email không có trong database
   - Verify error message

3. **Test database connection:**
   - Sử dụng debug endpoints
   - Check response time

### Monitoring:

- Monitor database connection pool
- Check application memory usage
- Verify service uptime
- Monitor error rates 