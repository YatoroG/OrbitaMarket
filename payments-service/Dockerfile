FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./

COPY payments-service/build.gradle.kts ./payments-service/

RUN gradle :payments-service:dependencies --no-daemon

COPY payments-service/src ./payments-service/src

RUN gradle :payments-service:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN groupadd -r appgroup && useradd -r -g appgroup appuser

COPY --from=builder --chown=appuser:appgroup /app/payments-service/build/libs/*.jar app.jar

EXPOSE 8082

USER appuser

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]