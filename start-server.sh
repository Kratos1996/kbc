#!/usr/bin/env bash
# start-server.sh — Run entire KBC server stack (Docker MySQL + Prisma + Seed + Node)

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/server"
cd "$ROOT_DIR"

echo "🐳 Starting MySQL via Docker Compose..."
docker compose -f ../infra/docker-compose.yml up -d mysql

echo "⏳ Waiting for MySQL to be healthy..."
until docker exec kbc-mysql mysqladmin ping -h localhost -uroot -proot_secret --silent 2>/dev/null; do
  sleep 1
done

echo "📦 Installing Node dependencies..."
npm ci 2>/dev/null || npm install

echo "🔧 Generating Prisma Client..."
npx prisma generate

echo "🗄️ Running Prisma migrations..."
npx prisma migrate deploy

echo "🌱 Seeding database (categories + questions)..."
npm run seed

echo "🚀 Starting KBC API server on http://localhost:4000 (Swagger at /docs)..."
npm run dev