/**
 * KBC prize ladder (in INR) and rules.
 * Index 0 is question 1 (easiest); index 14 is the top prize.
 * Safe zones: index 4 (₹10,000) and index 9 (₹3,20,000) — fall-back to these on a wrong answer.
 */
export const PRIZE_LADDER: readonly number[] = [
  1_000, 2_000, 3_000, 5_000, 10_000,
  20_000, 40_000, 80_000, 1_60_000, 3_20_000,
  6_40_000, 12_50_000, 25_00_000, 50_00_000, 75_00_000,
];

export const SAFE_ZONE_INDICES: readonly number[] = [4, 9];
export const MAX_LEVEL = PRIZE_LADDER.length; // 15

/** Map a 1-based prize level (1..15) to a question difficulty bucket. */
export function difficultyForLevel(level: number): 1 | 2 | 3 {
  if (level <= 5) return 1;
  if (level <= 10) return 2;
  return 3;
}

/** Compute guaranteed winnings on a wrong answer. */
export function guaranteedWinnings(currentLevel: number): number {
  const highestSafe = [...SAFE_ZONE_INDICES].reverse().find((idx) => currentLevel > idx);
  if (highestSafe === undefined) return 0;
  return PRIZE_LADDER[highestSafe];
}

export function prizeForLevel(level: number): number {
  const idx = level - 1;
  if (idx < 0 || idx >= PRIZE_LADDER.length) return 0;
  return PRIZE_LADDER[idx];
}

export function isSafeZone(level: number): boolean {
  return SAFE_ZONE_INDICES.includes(level - 1);
}
