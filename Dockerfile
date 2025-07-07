# ===== STAGE 1: BUILD =====
FROM gradle:8.2.1-jdk17 AS build

WORKDIR /app

COPY gradle gradle
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x ./gradlew

COPY . .

RUN ./gradlew clean

# ✅ tambahkan ini supaya dependencies di-resolve dulu
RUN ./gradlew build --no-daemon --stacktrace --warning-mode all || true

RUN ./gradlew shadowJar --no-daemon --stacktrace --warning-mode all

# ===== STAGE 2: RUNTIME =====
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/build/libs/EventifyBackend-all.jar app.jar

CMD ["java", "-jar", "app.jar"]
