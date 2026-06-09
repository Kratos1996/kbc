<# 
.SYNOPSIS
    Start-KBCServer.ps1 — Run entire KBC server stack (Docker MySQL + Prisma + Seed + Node)

.DESCRIPTION
    1. Starts MySQL via Docker Compose
    2. Waits for MySQL to be healthy
    3. Installs Node dependencies
    4. Generates Prisma Client
    5. Runs Prisma migrations
    6. Seeds database (categories + questions)
    7. Starts the KBC API server on http://localhost:4000 (Swagger at /docs)
#>

param(
    [switch]$SkipDocker,
    [switch]$SkipSeed
)

$ErrorActionPreference = "Stop"
$rootDir = Join-Path (Split-Path -Parent $MyInvocation.MyCommand.Definition) "server"
Set-Location $rootDir

Write-Host "📂 Working in: $rootDir" -ForegroundColor Cyan

if (-not $SkipDocker) {
    Write-Host "🐳 Starting MySQL via Docker Compose..." -ForegroundColor Green
    docker compose -f ..\infra\docker-compose.yml up -d mysql

    Write-Host "⏳ Waiting for MySQL to be healthy..." -ForegroundColor Yellow
    $maxAttempts = 60
    $attempt = 0
    do {
        $attempt++
        $result = docker exec kbc-mysql mysqladmin ping -h localhost -uroot -proot_secret --silent 2>$null
        if ($LASTEXITCODE -eq 0) { break }
        Start-Sleep -Seconds 1
    } while ($attempt -lt $maxAttempts)

    if ($attempt -ge $maxAttempts) {
        Write-Error "MySQL did not become healthy in time"
        exit 1
    }
    Write-Host "✅ MySQL is ready" -ForegroundColor Green
}

Write-Host "📦 Installing Node dependencies..." -ForegroundColor Green
try {
    npm ci
} catch {
    npm install
}

Write-Host "🔧 Generating Prisma Client..." -ForegroundColor Green
npx prisma generate

Write-Host "🗄️ Running Prisma migrations..." -ForegroundColor Green
npx prisma migrate deploy

if (-not $SkipSeed) {
    Write-Host "🌱 Seeding database (categories + questions)..." -ForegroundColor Green
    npm run seed
}

Write-Host "🚀 Starting KBC API server on http://localhost:4000 (Swagger at /docs)..." -ForegroundColor Green
npm run dev