#!/bin/bash

echo "Building Spring Boot application..."

# Clean and build
./mvnw clean package -DskipTests

echo "Build completed successfully!"

# Check if build was successful
if [ -f "target/mailapp-0.0.1-SNAPSHOT.jar" ]; then
    echo "JAR file created successfully!"
    echo "File size: $(ls -lh target/mailapp-0.0.1-SNAPSHOT.jar | awk '{print $5}')"
else
    echo "ERROR: JAR file not found!"
    exit 1
fi

echo "Ready for deployment to Render.com!" 