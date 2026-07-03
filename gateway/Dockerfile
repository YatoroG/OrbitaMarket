FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./

COPY gateway/build.gradle.kts ./gateway/

RUN gradle :gateway:dependencies --no-daemon

COPY gateway/src ./gateway/src

RUN gradle :gateway:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN groupadd -r appgroup && useradd -r -g appgroup appuser

COPY --from=builder --chown=appuser:appgroup /app/gateway/build/libs/*.jar app.jar

EXPOSE 8080

USER appuser

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]