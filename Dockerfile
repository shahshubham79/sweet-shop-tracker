FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/tracker-1.0.0.jar app.jar

EXPOSE 9090

HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:9090/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]