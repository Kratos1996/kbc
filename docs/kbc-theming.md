# KBC Theming Guide

Goal: replicate the iconic "Crorepati" look — cinematic dark blue stage, gold accents, hex option tiles, dramatic reveal animations.

## Palette (`ui/theme/Color.kt`)

| Token | Hex | Use |
|---|---|---|
| `DeepBlue` | `#0A1A4F` | Stage background, top bar |
| `RoyalPurple` | `#1F1147` | Secondary surfaces |
| `Gold` | `#FFD700` | Primary accent, current ladder level, button labels |
| `GoldDark` | `#B8860B` | Pressed/secondary gold |
| `Surface` | `#0F1B3D` | Cards, prize ladder background |
| `SurfaceVariant` | `#1B2454` | Inactive ladder rows, chips |
| `OnSurface` | `#FFFFFF` | Body text |
| `OnSurfaceMuted` | `#B0BEC5` | Secondary text |
| `ErrorRed` | `#E53935` | Wrong answer, error states |
| `SuccessGreen` | `#43A047` | Correct answer, win |
| `SafeZone` | `#FFB300` | Safe-zone highlight |

## Typography

`KbcTypography` in `ui/theme/Type.kt` uses system fonts for now. For the KBC look:
- Headlines: bold, letter-spaced
- Prize ladder: monospace numbers
- Question text: large (22-28sp), bold

Add a custom display font by dropping a TTF into `res/font/` and registering it in `Type.kt`:
```kotlin
val KbcFont = FontFamily(Font(R.font.kbc_display))
```

## Components

### `PrizeLadder`
- Vertical list, level 15 at top, level 1 at bottom
- Current level highlighted in gold
- Safe zones (5, 10) marked with a subtle SafeZone tint
- Indian number formatting (`1,00,000` / `1.5 Cr`)

### `LifelineBar`
- 5 chips: 50:50, Audience Poll, Phone-a-Friend, Ask the Expert, Flip the Question
- Used lifelines dim out

### `OptionTile` (in `GameScreen`)
- Hex-style rounded rectangle
- Option letter (A/B/C/D) in a small gold square on the left
- Full text on the right
- Tap → lock → confirm (Phase 2)

## Sound (Phase 3)

Place MP3/OGG in `res/raw/`:
- `lock.mp3` — option selected
- `correct.mp3` — right answer fanfare
- `wrong.mp3` — wrong answer dramatic sting
- `fanfare.mp3` — Crorepati celebration

Wire up via `SoundManager` in `audio/SoundManager.kt` (SoundPool).

## Animations (Phase 3)

- **Lottie** for lifelines, reveal, walk-away, victory (place JSON in `res/raw/` and load with `LottieAnimation`).
- **Compose `animateColorAsState`** for state changes (correct/wrong flash on tiles).
- **Compose `AnimatedContent`** for question cross-fades.

## Dark mode

`values-night/themes.xml` uses black status/nav bars for an immersive feel.
