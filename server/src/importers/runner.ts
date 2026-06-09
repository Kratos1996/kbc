import { PrismaClient } from '@prisma/client';
import fs from 'fs';
import path from 'path';
import { detectAndNormalize } from './index';

export async function importQuestionsFromDir(dir: string, prisma: PrismaClient) {
  if (!fs.existsSync(dir)) return { inserted: 0, skipped: 0, files: 0 };
  const files = fs
    .readdirSync(dir)
    .filter((f) => f.endsWith('.json'))
    .map((f) => path.join(dir, f));
  let inserted = 0;
  let skipped = 0;
  for (const file of files) {
    const raw = fs.readFileSync(file, 'utf-8');
    const json = JSON.parse(raw) as unknown;
    const normalized = detectAndNormalize(json, path.basename(file));
    for (const q of normalized) {
      const categorySlug = q.categorySlug ?? 'general';
      const category = await prisma.category.upsert({
        where: { slug: categorySlug },
        update: {},
        create: { name: q.categoryName ?? categorySlug, slug: categorySlug },
      });
      const exists = await prisma.question.findFirst({
        where: { text: q.text, categoryId: category.id },
      });
      if (exists) { skipped += 1; continue; }
      await prisma.question.create({
        data: {
          text: q.text,
          optionA: q.options[0],
          optionB: q.options[1],
          optionC: q.options[2],
          optionD: q.options[3],
          correctOption: q.correctIndex,
          difficulty: q.difficulty,
          categoryId: category.id,
          explanation: q.explanation ?? null,
          source: q.source,
          isApproved: true,
        },
      });
      inserted += 1;
    }
  }
  return { inserted, skipped, files: files.length };
}
