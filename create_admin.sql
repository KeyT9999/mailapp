-- Tạo admin user nếu chưa tồn tại
-- Email: trankimthang857@gmail.com
-- Password: 123
-- Role: admin

-- Kiểm tra xem user đã tồn tại chưa
IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'trankimthang857@gmail.com')
BEGIN
    INSERT INTO users (email, password_hash, role, username) 
    VALUES (
        'trankimthang857@gmail.com', 
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa',
        'admin',
        'admin'
    );
    PRINT 'Admin user created successfully';
END
ELSE
BEGIN
    -- Cập nhật role nếu user đã tồn tại
    UPDATE users 
    SET role = 'admin', 
        password_hash = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa'
    WHERE email = 'trankimthang857@gmail.com';
    PRINT 'Admin user updated successfully';
END

-- Kiểm tra kết quả
SELECT id, email, password_hash, role, username FROM users WHERE email = 'trankimthang857@gmail.com'; 