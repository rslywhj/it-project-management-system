# 构建阶段
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY pm-common ./pm-common
COPY pm-auth ./pm-auth
COPY pm-project ./pm-project
COPY pm-requirement ./pm-requirement
COPY pm-task ./pm-task
COPY pm-milestone ./pm-milestone
COPY pm-promotion ./pm-promotion
COPY pm-delivery ./pm-delivery
COPY pm-test ./pm-test
COPY pm-notification ./pm-notification
COPY pm-report ./pm-report
COPY pm-lowcode ./pm-lowcode
COPY pm-integration ./pm-integration
COPY pm-knowledge ./pm-knowledge
COPY pm-resource ./pm-resource
COPY pm-risk ./pm-risk
COPY pm-boot ./pm-boot
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/pm-boot/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
