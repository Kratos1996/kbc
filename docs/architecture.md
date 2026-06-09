# KBC Crorepati — Architecture

## High-level

```
┌──────────────────────┐    HTTPS    ┌─────────────────────────┐
│ Android (Kotlin/     │ ──────────▶ │ Node.js + Express (TS)  │
│  Compose, MVVM)      │             │  REST + Socket.IO       │
│  Hilt · Retrofit ·   │ ◀────────── │  Prisma · Zod · Pino    │
│  Room · Play Billing │  WSS/JSON   └─────────────┬───────────┘
└──────────────────────┘                          │
                                                  ▼
                                       ┌──────────────────┐
                                       │   MySQL 8        │
                                       │   (Docker)       │
                                       └──────────────────┘
```

## Layers

### Android (`android/`)

- **UI** — Jetpack Compose, MVVM with ViewModels exposing `StateFlow<UiState>`.
- **Domain** — pure-Kotlin models, repository interfaces, use-cases.
- **Data** — repository implementations calling `KbcApi` (Retrofit), persisting session tokens in `PreferencesManager` (DataStore), caching recent questions in Room.
- **DI** — Hilt with `@HiltAndroidApp` `KbcApplication`.
- **Networking** — `AuthInterceptor` adds Bearer, `TokenAuthenticator` refreshes on 401.
- **Realtime** — Socket.IO client for live 1v1 (Phase 5).
- **Billing** — Google Play Billing v7 with server-side receipt verification.
- **Analytics** — Firebase Analytics + Crashlytics.

### Server (`server/`)

- **Express** app with versioned routes under `/api/v1`.
- **Controllers → Services → Repositories** layered architecture.
- **Zod** validators gate every input.
- **JWT** access (15m) + refresh (30d) with rotation; refresh tokens stored hashed in MySQL.
- **Prisma** for type-safe DB access; `schema.prisma` is the source of truth.
- **Socket.IO** for live multiplayer.
- **node-cron** job generates the daily challenge at 00:05 IST.
- **Google Play API** for verifying in-app purchase tokens.
- **pino** for structured JSON logging.
- **Swagger UI** at `/docs`.

### Database (MySQL 8)

Schema: `users`, `refresh_tokens`, `categories`, `questions`, `games`, `game_answers`, `lifeline_uses`, `daily_challenges`, `daily_questions`, `daily_submissions`, `leaderboard_entries`, `friendships`, `multiplayer_matches`, `multiplayer_players`, `iap_receipts`.

## KBC Game Rules (implemented)

- 15 levels with the canonical prize ladder (₹1,000 → ₹75,00,000).
- Safe zones at levels 5 (₹10,000) and 10 (₹3,20,000).
- Difficulty mapped: levels 1-5 = easy, 6-10 = medium, 11-15 = hard.
- 5 lifelines (one use each per classic game): 50:50, Audience Poll, Phone-a-Friend, Ask-the-Expert, Flip-the-Question.
- Wrong answer → fall back to the highest safe zone reached.

## Phases

1. ✅ Foundation (auth, schema, KBC theme, login/register) — **this scaffold**
2. Core gameplay (categories, classic mode, lifelines)
3. Polish (sounds, Lottie, animations, confetti)
4. Daily + leaderboards + friends
5. Multiplayer (async + live 1v1)
6. Monetization (Play Billing) + Analytics
