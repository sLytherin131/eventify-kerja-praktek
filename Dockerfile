# ===== STAGE 1: BUILD =====
FROM gradle:8.2.1-jdk17 AS build

WORKDIR /app

# Copy file Gradle wrapper & configs lebih dulu untuk cache layer build
COPY gradle gradle
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copy semua source code
COPY . .

# Bangun shadow JAR
RUN ./gradlew shadowJar --no-daemon

# ===== STAGE 2: RUNTIME =====
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/build/libs/EventifyBackend-0.0.1.jar app.jar

CMD ["java", "-jar", "app.jar"]
