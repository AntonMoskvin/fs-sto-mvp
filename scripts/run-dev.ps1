param()
Write-Host 'Starting Postgres (local Docker) and dev Spring Boot app (Gradle) in B mode...'

docker-compose down
docker-compose up -d
docker-compose build
docker-compose up -d db
Start-Sleep -Seconds 6

$env:SPRING_PROFILES_ACTIVE = 'postgres'
$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5432/sfsto'
$env:SPRING_DATASOURCE_USERNAME = 'postgres'
$env:SPRING_DATASOURCE_PASSWORD = 'postgres'

./gradlew bootRun
