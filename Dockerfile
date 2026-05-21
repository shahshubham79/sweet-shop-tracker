# Stage 1: Build the JAR with Maven
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Copy only Maven metadata and download dependencies first (cacheable)
COPY pom.xml .
# If you have a settings.xml or .mvn, copy them as needed

# Copy source
COPY src ./src

# Build (skip tests to speed up CI if desired)
RUN mvn -B -DskipTests package

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy jar from builder stage (adjust name if your final artifact has a different name)
COPY --from=builder /workspace/target/tracker-1.0.0.jar app.jar

EXPOSE 9090

# Optional healthcheck (Render will still monitor service)
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:9090/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]