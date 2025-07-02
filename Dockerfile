# Gunakan image JDK
FROM gradle:8.2.1-jdk17 AS build

WORKDIR /app
COPY . .

# Bangun shadow jar
RUN gradle shadowJar

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /app/build/libs/EventifyBackend-0.0.1.jar app.jar

CMD ["java", "-jar", "app.jar"]
