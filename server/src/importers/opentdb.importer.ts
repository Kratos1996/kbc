import { NormalizedQuestion } from './types';

const CATEGORY_SLUGS: Record<string, string> = {
  'General Knowledge': 'general',
  'Science & Nature': 'science',
  'Science: Mathematics': 'math',
  'Science: Nature': 'science',
  'History': 'history',
  'Geography': 'geography',
  'Sports': 'sports',
  'Entertainment: Film': 'entertainment',
  'Entertainment: Music': 'entertainment',
  'Entertainment: Television': 'entertainment',
  'Entertainment: Books': 'literature',
  'Entertainment: Video Games': 'entertainment',
  'Entertainment: Board Games': 'entertainment',
  'Entertainment: Comics': 'entertainment',
  'Entertainment: Japanese Anime & Manga': 'entertainment',
  'Entertainment: Musicals & Theatres': 'entertainment',
  'Entertainment: Cartoon & Animations': 'entertainment',
  'Art': 'art',
  'Animals': 'science',
  'Vehicles': 'general',
  'Politics': 'history',
  'Celebrities': 'entertainment',
};

const DIFF_MAP: Record<string, 1 | 2 | 3> = { easy: 1, medium: 2, hard: 3 };

function decodeHtml(s: string): string {
  return s
    .replace(/&quot;/g, '"')
    .replace(/&#039;/g, "'")
    .replace(/&apos;/g, "'")
    .replace(/&amp;/g, '&')
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/&eacute;/g, 'é')
    .replace(/&nbsp;/g, ' ');
}

export function normalizeOpenTdb(payload: { results: Array<Record<string, unknown>> }): NormalizedQuestion[] {
  return payload.results
    .map((r): NormalizedQuestion | null => {
      const text = decodeHtml(String(r.question ?? ''));
      const correct = decodeHtml(String(r.correct_answer ?? ''));
      const incorrect = Array.isArray(r.incorrect_answers) ? r.incorrect_answers.map((s) => decodeHtml(String(s))) : [];
      if (!text || !correct || incorrect.length !== 3) return null;
      const all = [...incorrect, correct];
      // Shuffle deterministically by question text so client/server agree? Actually client just needs
      // a stable mapping per question; for simplicity, keep order: insert correct at index based on hash.
      const correctIndex = (text.length + correct.length) % 4;
      const options: [string, string, string, string] = ['', '', '', ''];
      const remaining = all.filter((_, i) => i !== 3); // incorrect
      // Spread incorrect into non-correct slots
      const slotOrder = [0, 1, 2, 3].filter((i) => i !== correctIndex);
      slotOrder.forEach((slot, i) => (options[slot] = remaining[i]));
      options[correctIndex] = correct;
      const cat = String(r.category ?? '');
      const slug = CATEGORY_SLUGS[cat] ?? 'general';
      const difficulty = DIFF_MAP[String(r.difficulty ?? 'easy').toLowerCase()] ?? 1;
      return {
        text,
        options,
        correctIndex: correctIndex as 0 | 1 | 2 | 3,
        difficulty,
        categorySlug: slug,
        categoryName: cat,
        source: 'opentdb',
      };
    })
    .filter((q): q is NormalizedQuestion => q !== null);
}
