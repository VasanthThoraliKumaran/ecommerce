# Use Eclipse Temurin Java 24 as base image
FROM eclipse-temurin:24-jdk-alpine

# Set work directory
WORKDIR /app

# Copy the JAR file (replace if you renamed JAR)
COPY target/ecommerce-0.0.1-SNAPSHOT.jar /app/ecommerce.jar

# Expose port (change if your app runs on different port)
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "/app/ecommerce.jar"]
