# Dataset Format

The seed importer in `server/src/importers/` auto-detects two JSON shapes.
Drop your files into `server/seeds/questions/` and run:

```bash
cd server
npm run seed         # imports questions + creates default categories
# or
npm run import:questions   # imports only, skips default categories
```

## Format A — KBC-native (recommended)

Top-level **array** of question objects.

```json
[
  {
    "id": "kbc-gen-001",
    "question": "What is the capital of India?",
    "options": ["Mumbai", "New Delhi", "Kolkata", "Chennai"],
    "correctIndex": 1,
    "difficulty": 1,
    "category": "general",
    "explanation": "New Delhi has been the capital of India since 1947."
  }
]
```

Fields:
- `question` (or `text`) — required
- `options` — array of 4 strings — required
- `correctIndex` (or `correct_index` or `answer`) — 0..3 — required
- `difficulty` — `1`, `2`, `3` (or `easy`, `medium`, `hard`) — defaults to `1`
- `category` — slug (`general`, `science`, `history`, `geography`, `sports`, `entertainment`, `literature`, `math`) — defaults to `general`. Unknown slugs are auto-created.
- `explanation` — optional

## Format B — OpenTriviaDB response

Top-level **object** with a `results` array (as returned by `https://opentdb.com/api.php`).

```json
{
  "response_code": 0,
  "results": [
    {
      "category": "General Knowledge",
      "type": "multiple",
      "difficulty": "easy",
      "question": "What is the capital of France?",
      "correct_answer": "Paris",
      "incorrect_answers": ["London", "Berlin", "Madrid"]
    }
  ]
}
```

Mapping:
- `difficulty: easy/medium/hard` → 1/2/3
- `category` is mapped to a slug (e.g. `Entertainment: Film` → `entertainment`); unmapped categories fall back to `general`
- HTML entities (`&quot;`, `&#039;`, etc.) are decoded
- Correct answer is placed in a deterministic slot so the same source data yields the same option order on the client

## Difficulty → prize level

```
Level  1- 5 → difficulty bucket "easy"   (1)
Level  6-10 → difficulty bucket "medium" (2)
Level 11-15 → difficulty bucket "hard"   (3)
```

The mapping lives in `server/src/utils/prize-ladder.ts` and is configurable.
