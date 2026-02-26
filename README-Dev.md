Dev Mode with Docker (Postgres) + Gradle

Prerequisites:
- Docker Desktop (with WSL2 on Windows recommended)
- Git installed

What this workflow does:
- Starts a Postgres container (PostGIS optional) using docker-compose
- Runs the Spring Boot app locally via Gradle with a Postgres data source
- Uses a dedicated Postgres profile for migrations and data sources

How to run:
- Start Postgres and app (Linux/macOS):
  bash scripts/run-dev.sh
- Start Postgres and app (Windows PowerShell):
  powershell -ExecutionPolicy Bypass -File scripts/run-dev.ps1

Notes:
- The app connects to localhost:5432 for Postgres in this mode. Ensure the container is up.
- Flyway migrations will run on startup for the profile postgres.
- To stop containers: docker-compose down

After startup you can open:
- UI: http://localhost:8080/
- Ping: http://localhost:8080/api/v1/ping
