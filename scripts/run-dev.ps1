param()
Write-Host 'Starting Postgres (local Docker) and dev Spring Boot app (Gradle) in B mode...'

docker-compose down
docker-compose build
docker-compose up -d --build db app
Start-Sleep -Seconds 6

$env:SPRING_PROFILES_ACTIVE = 'postgres'
$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5432/sfsto'
$env:SPRING_DATASOURCE_USERNAME = 'postgres'
$env:SPRING_DATASOURCE_PASSWORD = 'postgres'

# Start the app container via docker-compose (no host bootRun)
# Wait a bit for the app to initialize
Start-Sleep -Seconds 6
