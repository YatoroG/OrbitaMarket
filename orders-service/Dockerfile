FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./

COPY orders-service/build.gradle.kts ./orders-service/

RUN gradle :orders-service:dependencies --no-daemon

COPY orders-service/src ./orders-service/src

RUN gradle :orders-service:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN groupadd -r appgroup && useradd -r -g appgroup appuser

COPY --from=builder --chown=appuser:appgroup /app/orders-service/build/libs/*.jar app.jar

EXPOSE 8082

USER appuser

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]