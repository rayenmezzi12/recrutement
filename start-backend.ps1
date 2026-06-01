# Demarrage stack microservices PFA (Eureka + Gateway + services)
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$mvn = "C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.1.1\plugins\maven\lib\maven3\bin\mvn.cmd"

Write-Host "1/2 Docker : PostgreSQL + RabbitMQ..." -ForegroundColor Cyan
Set-Location $root
docker compose up -d postgres rabbitmq

$common = "-Dspring-boot.run.jvmArguments=-Deureka.client.service-url.defaultZone=http://localhost:8761/eureka/"

Write-Host "2/2 Services Spring (fenetres separees)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\backend\eureka-server'; & '$mvn' spring-boot:run"
Start-Sleep -Seconds 8
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\backend\authentification'; & '$mvn' spring-boot:run $common '-Dspring-boot.run.profiles=local'"
Start-Sleep -Seconds 3
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\backend\logique-metier'; & '$mvn' spring-boot:run $common '-Dspring-boot.run.profiles=local'"
Start-Sleep -Seconds 3
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\backend\notification-service'; & '$mvn' spring-boot:run $common"
Start-Sleep -Seconds 3
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\backend\chatbotIA'; & '$mvn' spring-boot:run $common"
Start-Sleep -Seconds 3
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\backend\api-gateway'; & '$mvn' spring-boot:run $common"

Write-Host ""
Write-Host "Gateway : http://localhost:8080" -ForegroundColor Green
Write-Host "Eureka  : http://localhost:8761" -ForegroundColor Green
Write-Host "Frontend: cd frontend; npm start  (proxy -> 8080)" -ForegroundColor Green
