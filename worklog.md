# KBC App — Session Summary

## What we did

- **Fastest Finger First (FFF) screen**: Full minigame — sorting challenge, 4 hex cards, countdown timer, selection order badges, submit + result overlay with retry.
- **Pre-show Countdown screen**: Glass-panel lobby — live timer, prize pool growth bar, contestant spotlights (3 profiles), match parameters, lifeline preview, particle background.
- **Achievement Gallery screen**: Bento grid of hex badges (earned/locked), mastery progress bar, recent milestones cards, detail modal with share. `AchievementRepository` + `AchievementUseCase`.
- **Match History screen**: Scrollable win/loss list, highlight entry for special matches, stats card (win rate bar + total earnings). `MatchHistoryRepository` + `MatchHistoryUseCase`.
- **Tournament Registration screen**: Hero with $1M prize pool + countdown, entry requirements grid, prize breakdown, RP/Gold Pass entry, loading state.
- **Polish pass**: `ShimmerOverlay` + `PulseGlowProgress` in shared `Animations.kt`, applied to HomeScreen PLAY button and achievement mastery bar.
- **Dead code removal**: Deleted unused `GameRepository.kt`, stale imports.
- **Social auth stubs**: Wired Google/Facebook/OTP/ForgotPassword in `AuthViewModel`, `LoginScreen` calls all four.
- **UseCase layer**: Created 9 UseCase classes — `AuthUseCase`, `FffUseCase`, `AchievementUseCase`, `MatchHistoryUseCase`, `ShopUseCase`, `ProfileUseCase`, `MultiplayerUseCase`, `LeaderboardUseCase`, `DailyUseCase`. All 9 ViewModels refactored to inject UseCases instead of Repositories. Method names aligned with repository interfaces.
- **Repository bindings**: `FffRepositoryImpl`, `AchievementRepositoryImpl`, `MatchHistoryRepositoryImpl` registered in Hilt `RepositoryModule`.
- **Domain model migration**: `Achievement`, `Milestone`, `MatchEntry` moved to `domain/model/`. Old UI-local copies deleted.
- **Backend running**: Docker MySQL (`kbc-mysql`), Prisma migration + seed (24 questions, 8 categories), KBC API server on `http://localhost:4000`.

## Architecture
```
ViewModel → UseCase (@Inject) → Repository (interface) → RepositoryImpl (@Singleton @Binds)
```
9 UseCases, each wrapping a single repository. ViewModels never touch repositories directly.

## To build
```powershell
cd android && .\gradlew.bat :app:assembleDebug
```

## Backend
Server at `http://localhost:4000` (PowerShell background job `kbcServer`). Stop with `Stop-Job -Name kbcServer`.
