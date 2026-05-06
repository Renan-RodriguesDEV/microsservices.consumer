# ============================================================================
# Stage 1: Build the application with Maven
# ============================================================================
FROM maven:3.9-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy Maven configuration files
COPY pom.xml .

# Copy source code
COPY src ./src

# Build the JAR file, skipping tests for faster build
RUN mvn clean package -DskipTests

# ============================================================================
# Stage 2: Create the final lightweight image
# ============================================================================
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Declare exposed port
EXPOSE 8081

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=production \
    JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

# Copy the built JAR from the first stage
COPY --from=build /app/target/*.jar app.jar

# Health check to verify application is running
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8081/auth || exit 1

# Run the application with proper signal handling
CMD java $JAVA_OPTS -jar app.jar
