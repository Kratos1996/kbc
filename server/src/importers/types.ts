export interface NormalizedQuestion {
  text: string;
  options: [string, string, string, string];
  correctIndex: 0 | 1 | 2 | 3;
  difficulty: 1 | 2 | 3;
  categorySlug: string;
  categoryName?: string;
  explanation?: string;
  source: 'kbc-native' | 'opentdb' | 'custom';
}
