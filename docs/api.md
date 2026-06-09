# KBC Crorepati — REST API

Base URL: `http://localhost:4000` (dev) — all routes prefixed with `/api/v1`.

Auth: `Authorization: Bearer <accessToken>` (skip for `/auth/*`).

Interactive docs at **`/docs`** (Swagger UI).

## Auth

| Method | Path | Body | Description |
|---|---|---|---|
| POST | `/auth/register` | `{ email, username, password, displayName? }` | Create account, return user + tokens |
| POST | `/auth/login` | `{ emailOrUsername, password }` | Login, return user + tokens |
| POST | `/auth/refresh` | `{ refreshToken }` | Rotate refresh, return new tokens |
| POST | `/auth/logout` | `{ refreshToken }` | Revoke refresh |
| GET  | `/auth/me` | — | Current user (auth required) |

## Categories & Questions

| Method | Path | Description |
|---|---|---|
| GET | `/categories` | List categories |
| GET | `/questions/next?level=N&categoryId=...` | Pick a question for level 1-15 (auth) |
| GET | `/questions/:id?reveal=true` | Get question (auth; reveal=1 shows answer) |

## Games

| Method | Path | Description |
|---|---|---|
| POST | `/games` | Start new game. Body: `{ mode: classic|quick|category|practice, categoryId?, totalQuestions? }` |
| GET  | `/games/:id/question` | Get current question for the game |
| POST | `/games/:id/answer` | Answer. Body: `{ questionId, chosenOption, timeMs }` |
| POST | `/games/:id/quit` | Walk away; returns guaranteed winnings |

## Daily Challenge

| Method | Path | Description |
|---|---|---|
| GET  | `/daily/today` | Today's 10-question set |
| POST | `/daily/submit` | Body: `{ dailyId, answers: [{questionId, chosenOption}] }` |

## Leaderboard

| Method | Path | Description |
|---|---|---|
| GET | `/leaderboard?scope=global|weekly|monthly|friends&limit=50` | Top scores |

## Users

| Method | Path | Description |
|---|---|---|
| GET  | `/users/me` | Profile |
| PATCH | `/users/me` | Update `displayName`, `avatarUrl` |

## Billing (Play Billing)

| Method | Path | Description |
|---|---|---|
| GET  | `/billing/products` | List product catalog |
| POST | `/billing/verify` | Body: `{ productId, purchaseToken }`. Server verifies with Google and grants. |

## Multiplayer (REST + Socket.IO)

| Method | Path | Description |
|---|---|---|
| POST | `/multiplayer/async/create` | Create async match, returns invite code |
| GET  | `/multiplayer/:id` | Match details |

Socket.IO events: `mp:create`, `mp:join`, `mp:answer`, `mp:start`, `mp:question`, `mp:score`, `mp:end`, `mp:players`.

## Error format

```json
{ "error": { "code": "BAD_REQUEST", "message": "Validation failed", "details": { } } }
```
