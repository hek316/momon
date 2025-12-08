FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copy Gradle wrapper and dependencies
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src src

# Build application
RUN ./gradlew clean bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy built jar from build stage (use exact filename pattern)
COPY --from=build /app/build/libs/momon-backend-*.jar app.jar

EXPOSE 8080

# Virtual Threads are automatically enabled via application.yml (spring.threads.virtual.enabled: true)
ENTRYPOINT ["java", "-jar", "app.jar"]
