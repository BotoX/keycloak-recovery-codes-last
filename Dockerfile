# --- Build stage ---
FROM maven:3.9.15-eclipse-temurin-21 AS builder
WORKDIR /build

# Cache dependencies
COPY pom.xml .
RUN mvn -B -q -e dependency:go-offline

# Build
COPY src ./src
RUN mvn -B -q clean install

# --- Runtime stage ---
FROM busybox:stable
WORKDIR /provider

COPY --from=builder /build/target/keycloak-recovery-codes-last-1.0.0.jar /provider/keycloak-recovery-codes-last.jar
