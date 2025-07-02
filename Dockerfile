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

# Copy semua source code lainnya
COPY . .

# Jalankan shadowJar untuk membangun fat-jar
RUN ./gradlew shadowJar --no-daemon

# ===== STAGE 2: RUNTIME =====
FROM eclipse-temurin:17-jre

WORKDIR /app

# Gunakan fat-jar hasil shadowJar
COPY --from=build /app/build/libs/EventifyBackend-all.jar app.jar

# Jalankan aplikasi
CMD ["java", "-jar", "app.jar"]
