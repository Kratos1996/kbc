# KBC Crorepati Quiz

A mobile quiz game inspired by **Kaun Banega Crorepati** (Who Wants to Be a Millionaire).

- **Android app** (Kotlin + Jetpack Compose)
- **Backend API** (Node.js + Express + TypeScript)
- **MySQL 8** for persistence
- **JWT auth**, **Socket.IO** for live multiplayer
- **Google Play Billing** for monetization

## Repo layout

```
kbc-app/
├── android/   # Native Android app (Kotlin, Compose)
├── server/    # Node.js + Express + Prisma API
├── infra/     # Docker Compose (MySQL, Adminer)
├── docs/      # Architecture, API, dataset, theming
└── .github/   # CI workflows
```

## Quick start

### 1. Start MySQL
```bash
cd infra
docker compose up -d mysql
```

### 2. Run the backend
```bash
cd server
cp .env.example .env       # edit values
npm install
npx prisma migrate dev
npx prisma generate
npm run seed               # optional: import sample questions
npm run dev
```

API at `http://localhost:4000` (Swagger at `/docs`).

### 3. Run the Android app
```bash
cd android
# First-time only: bootstrap the Gradle wrapper jar (Android Studio does this automatically)
gradle wrapper --gradle-version 8.9

cp local.properties.example local.properties
# Edit local.properties to set sdk.dir to your Android SDK path
# Optional: set API_BASE_URL (default http://10.0.2.2:4000 for the Android emulator)

gradlew.bat :app:assembleDebug
```

Or open the `android/` folder in **Android Studio** and let it sync & generate the wrapper.

## Documentation
- [docs/architecture.md](docs/architecture.md)
- [docs/api.md](docs/api.md)
- [docs/dataset-format.md](docs/dataset-format.md)
- [docs/kbc-theming.md](docs/kbc-theming.md)

## License
MIT
