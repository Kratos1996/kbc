import { NormalizedQuestion } from './types';

const DIFFICULTY_MAP: Record<string, 1 | 2 | 3> = {
  '1': 1, '2': 2, '3': 3,
  easy: 1, medium: 2, hard: 3,
};

export function normalizeKbcNative(items: Array<Record<string, unknown>>): NormalizedQuestion[] {
  return items
    .map((item): NormalizedQuestion | null => {
      const text = String(item.question ?? item.text ?? '').trim();
      const options = item.options;
      if (!text || !Array.isArray(options) || options.length !== 4) return null;
      const correctIndex = Number(item.correctIndex ?? item.correct_index ?? item.answer);
      if (Number.isNaN(correctIndex) || correctIndex < 0 || correctIndex > 3) return null;
      const diffRaw = String(item.difficulty ?? '1').toLowerCase();
      const difficulty = DIFFICULTY_MAP[diffRaw] ?? 1;
      const categorySlug = String(item.category ?? 'general').toLowerCase().replace(/\s+/g, '-');
      return {
        text,
        options: [String(options[0]), String(options[1]), String(options[2]), String(options[3])],
        correctIndex: correctIndex as 0 | 1 | 2 | 3,
        difficulty,
        categorySlug,
        categoryName: typeof item.category === 'string' ? item.category : undefined,
        explanation: item.explanation ? String(item.explanation) : undefined,
        source: 'kbc-native',
      };
    })
    .filter((q): q is NormalizedQuestion => q !== null);
}
