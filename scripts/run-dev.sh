#!/usr/bin/env bash
set -euo pipefail
echo "Starting Postgres (local Docker) and dev Spring Boot app (Gradle) in B mode..."

docker-compose up -d db
echo "Waiting for DB to be ready..."
sleep 6

echo "Launching Spring Boot app (Gradle) with Postgres profile over localhost DB..."
SPRING_PROFILES_ACTIVE=postgres \
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/sfsto \
SPRING_DATASOURCE_USERNAME=postgres \
SPRING_DATASOURCE_PASSWORD=postgres \
./gradlew bootRun
