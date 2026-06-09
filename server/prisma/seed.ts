import { PrismaClient } from '@prisma/client';
import { importQuestionsFromDir } from '../src/importers';
import path from 'path';

const prisma = new PrismaClient();

const DEFAULT_CATEGORIES: Array<{ name: string; slug: string; icon: string; sortOrder: number }> = [
  { name: 'General Knowledge', slug: 'general', icon: 'globe', sortOrder: 0 },
  { name: 'Science & Nature', slug: 'science', icon: 'flask', sortOrder: 1 },
  { name: 'History', slug: 'history', icon: 'scroll', sortOrder: 2 },
  { name: 'Geography', slug: 'geography', icon: 'map', sortOrder: 3 },
  { name: 'Sports', slug: 'sports', icon: 'trophy', sortOrder: 4 },
  { name: 'Entertainment', slug: 'entertainment', icon: 'film', sortOrder: 5 },
  { name: 'Literature', slug: 'literature', icon: 'book', sortOrder: 6 },
  { name: 'Mathematics', slug: 'math', icon: 'calculator', sortOrder: 7 },
];

async function main() {
  console.log('Seeding categories...');
  for (const c of DEFAULT_CATEGORIES) {
    await prisma.category.upsert({
      where: { slug: c.slug },
      update: { name: c.name, icon: c.icon, sortOrder: c.sortOrder },
      create: c,
    });
  }

  const seedsDir = path.resolve(__dirname, '..', 'seeds', 'questions');
  console.log(`Importing questions from ${seedsDir} (skip if empty)...`);
  try {
    const result = await importQuestionsFromDir(seedsDir, prisma);
    console.log(`Imported ${result.inserted} (skipped ${result.skipped}) questions.`);
  } catch (err) {
    console.warn('Question import skipped:', (err as Error).message);
  }

  console.log('Seed complete.');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
