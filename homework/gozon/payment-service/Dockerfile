# Этап сборки (Builder)
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle :payment-service:build -x test --no-daemon

# Финальный этап (Runner)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/payment-service/build/libs/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]