# ===== STAGE 1: BUILD =====
FROM gradle:8.2.1-jdk17 AS build

WORKDIR /app

# Copy gradle wrapper files + settings
COPY gradle gradle
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Tambahkan permission eksekusi untuk gradlew
RUN chmod +x gradlew

# Copy source code lainnya
COPY . .

# Jalankan shadowJar
RUN ./gradlew shadowJar --no-daemon

# ===== STAGE 2: RUNTIME =====
FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /app/build/libs/EventifyBackend-0.0.1.jar app.jar

CMD ["java", "-jar", "app.jar"]
