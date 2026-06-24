# syntax=docker/dockerfile:1

# --- Stage 1: build the fat jar with Maven (no JDK/Maven needed on the host) ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Resolve dependencies first so they stay cached when only the source changes.
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
RUN mvn -q -B package

# --- Stage 2: slim runtime image with just a JRE + the built jar ---
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/target/java-marc4j-0.1.0.jar app.jar

# The app's default data path is ../marc-records/... relative to the work dir,
# which resolves to /marc-records once that volume is mounted (see compose).
ENTRYPOINT ["java", "-jar", "app.jar"]
