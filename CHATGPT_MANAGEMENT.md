# Hướng dẫn Quản lý Email ChatGPT

## Tính năng mới đã thêm

Admin giờ đây có thể quản lý đầy đủ các email ChatGPT và Secret Key thông qua giao diện web.

### Các chức năng có sẵn:

1. **Xem danh sách Email ChatGPT**
   - Truy cập: `/admin/chatgpt-accounts`
   - Hiển thị tất cả email ChatGPT với ID, Email và Secret Key
   - Có thể thực hiện các thao tác sửa/xóa

2. **Thêm Email ChatGPT mới**
   - Truy cập: `/admin/add-chatgpt-email`
   - Nhập email ChatGPT và Secret Key
   - Tự động hiển thị mã 2FA hiện tại sau khi thêm

3. **Chỉnh sửa Email ChatGPT**
   - Click nút "Sửa" trong danh sách
   - Cập nhật email và Secret Key
   - Tự động hiển thị mã 2FA mới sau khi cập nhật

4. **Xóa Email ChatGPT**
   - Click nút "Xóa" trong danh sách
   - Có xác nhận trước khi xóa

### Cách sử dụng:

1. **Đăng nhập với tài khoản Admin**
   - Truy cập trang đăng nhập
   - Sử dụng tài khoản có role "ADMIN"

2. **Truy cập Dashboard**
   - Sau khi đăng nhập, chuyển đến Dashboard
   - Click "Quản lý Email ChatGPT" để xem danh sách

3. **Thêm Email mới**
   - Click "Thêm Email ChatGPT Mới"
   - Nhập email và Secret Key
   - Click "Thêm email ChatGPT"

4. **Chỉnh sửa Email**
   - Trong danh sách, click "Sửa" bên cạnh email cần sửa
   - Cập nhật thông tin
   - Click "Cập nhật"

5. **Xóa Email**
   - Trong danh sách, click "Xóa" bên cạnh email cần xóa
   - Xác nhận trong hộp thoại

### Tính năng bảo mật:

- Tất cả thao tác đều yêu cầu đăng nhập với quyền Admin
- Secret Key được hiển thị an toàn trong giao diện
- Có xác nhận trước khi xóa dữ liệu
- Tự động tạo mã 2FA sau khi thêm/sửa

### Cấu trúc Database:

Bảng `chatgpt_accounts`:
- `id`: Primary key
- `chatgpt_email`: Email ChatGPT (unique)
- `secret_key`: Secret Key cho 2FA

### API Endpoints:

- `GET /admin/chatgpt-accounts` - Xem danh sách
- `GET /admin/edit-chatgpt-account?id={id}` - Form chỉnh sửa
- `POST /admin/update-chatgpt-account` - Cập nhật
- `POST /admin/delete-chatgpt-account` - Xóa
- `GET /admin/add-chatgpt-email` - Form thêm mới
- `POST /admin/add-chatgpt-email` - Thêm mới

### Lưu ý:

- Email sẽ được tự động chuẩn hóa (lowercase, trim)
- Secret Key phải hợp lệ để tạo mã 2FA
- Tất cả thay đổi đều được lưu vào database ngay lập tức
- Giao diện responsive, hoạt động tốt trên mobile 