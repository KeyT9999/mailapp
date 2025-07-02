Write-Host "Building Spring Boot application..." -ForegroundColor Green

# Clean and build
./mvnw clean package -DskipTests

Write-Host "Build completed!" -ForegroundColor Green

# Check if build was successful
if (Test-Path "target/mailapp-0.0.1-SNAPSHOT.jar") {
    $fileSize = (Get-Item "target/mailapp-0.0.1-SNAPSHOT.jar").Length
    $fileSizeMB = [math]::Round($fileSize / 1MB, 2)
    Write-Host "JAR file created successfully! Size: $fileSizeMB MB" -ForegroundColor Green
} else {
    Write-Host "ERROR: JAR file not found!" -ForegroundColor Red
    exit 1
}

Write-Host "Ready for deployment to Render.com!" -ForegroundColor Yellow
Write-Host "Please push your code to GitHub and deploy on Render.com" -ForegroundColor Yellow 