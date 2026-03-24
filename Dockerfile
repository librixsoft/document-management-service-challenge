# ── Stage 1: Build ──────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

# Cache dependencies first
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build the fat JAR (skip tests during image build)
COPY src ./src
RUN mvn package -DskipTests -q

# ── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /build/target/document-management-service.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-Xmx50m", "-Xms32m", \
  "-XX:MaxMetaspaceSize=120m", \
  "-XX:+UseSerialGC", \
  "-XX:+UseContainerSupport", \
  "-jar", "app.jar"]
