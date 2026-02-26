SFSTO MVP — Docker Quick Start

Prerequisites
- Docker Desktop (WSL2 on Windows recommended)
- Docker Compose (bundled with Docker Desktop)

What this provides
- A Postgres + PostGIS database (dockerized)
- The Spring Boot app running in Docker
- Local development with an isolated stack

Getting started
1) Start Docker Desktop
2) Run the stack
   docker-compose down
   docker-compose build
   docker-compose up -d
   .\scripts\run-dev.ps1
   docker ps
3) Verify services
   - API: http://localhost:8080/api/v1/ping
   - UI: http://localhost:8080/
4) Stop the stack
   docker-compose down

Notes
- The Postgres data is stored in a named volume sfsto-db-data and persists across restarts.
- Flyway migrations run on startup to initialize the schema. In test environments, you can disable migrations via profiles if needed.
