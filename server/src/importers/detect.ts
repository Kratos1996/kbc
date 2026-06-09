import { NormalizedQuestion } from './types';
import { normalizeKbcNative } from './kbc-native.importer';
import { normalizeOpenTdb } from './opentdb.importer';

export function detectAndNormalize(json: unknown, fileName: string): NormalizedQuestion[] {
  // OpenTriviaDB: { response_code, results: [...] }
  if (
    typeof json === 'object' &&
    json !== null &&
    'results' in json &&
    Array.isArray((json as { results: unknown }).results)
  ) {
    return normalizeOpenTdb(json as { results: Array<Record<string, unknown>> });
  }
  // KBC-native: array of items
  if (Array.isArray(json)) {
    return normalizeKbcNative(json as Array<Record<string, unknown>>);
  }
  // Single object fallback
  if (typeof json === 'object' && json !== null && 'question' in (json as object)) {
    return normalizeKbcNative([json as Record<string, unknown>]);
  }
  throw new Error(`Unrecognized question file format: ${fileName}`);
}
