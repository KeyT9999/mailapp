# Sử dụng image OpenJDK 17 nhỏ gọn
FROM openjdk:17-jdk-slim

# Tạo thư mục làm việc
WORKDIR /app

# Copy file jar đã build vào container
COPY target/*.jar app.jar

# Mở port 8080 (hoặc port app bạn dùng)
EXPOSE 8080

# Lệnh chạy app
ENTRYPOINT ["java", "-jar", "app.jar"] 