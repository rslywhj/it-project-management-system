# ---- Build Stage ----
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# Cache dependencies
COPY pom.xml .
COPY pm-*/pom.xml ./pm-*/pom.xml
RUN mvn dependency:go-offline -B

# Build application
COPY . .
RUN mvn clean package -DskipTests -B

# ---- Runtime Stage ----
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="IT Project Management Team"

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Copy built jar
COPY --from=builder /build/pm-boot/target/pm-boot-*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
