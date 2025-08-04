# FROM openjdk:17-jdk-alpine

# # Install bash for compatibility
# RUN apk add --no-cache bash

# # Create logs directory
# RUN mkdir -p /app/logs

# # Create application directory
# RUN mkdir /opt/server

# # Create user
# RUN adduser -D -h /opt/server crud

# # Set ownership
# RUN chown crud:crud -R /opt/server /app/logs

# # Set working directory
# WORKDIR /opt/server

# # Copy Maven wrapper and pom.xml first for better caching
# COPY pom.xml ./
# COPY .mvn .mvn
# COPY mvnw ./

# # Make mvnw executable
# RUN chmod +x mvnw

# # Download dependencies
# RUN ./mvnw dependency:go-offline -B

# # Copy source code
# COPY src ./src

# # Build the application
# RUN ./mvnw clean package -DskipTests

# # Switch to non-root user
# USER crud

# # Expose port
# EXPOSE 8080

# # Health check
# HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
#   CMD curl -f http://localhost:8080/actuator/health || exit 1
# # Run the application
# CMD ["java", "-jar", "target/crud-app-1.0.0.jar"]

# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -Dmaven.test.skip=true -X
# Stage 2: Run the application using a lightweight JDK image
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (optional - based on your app)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
