import { prisma } from '../config/db';
import { difficultyForLevel, MAX_LEVEL } from '../utils/prize-ladder';
import { NotFoundError, BadRequestError } from '../utils/errors';

export interface PublicQuestion {
  id: string;
  text: string;
  options: string[]; // [a, b, c, d]
  difficulty: number;
  categoryId: string;
  categoryName: string;
}

function toPublic(q: {
  id: string;
  text: string;
  optionA: string;
  optionB: string;
  optionC: string;
  optionD: string;
  difficulty: number;
  categoryId: string;
  category: { name: string };
}): PublicQuestion {
  return {
    id: q.id,
    text: q.text,
    options: [q.optionA, q.optionB, q.optionC, q.optionD],
    difficulty: q.difficulty,
    categoryId: q.categoryId,
    categoryName: q.category.name,
  };
}

export async function listCategories() {
  return prisma.category.findMany({ orderBy: { sortOrder: 'asc' } });
}

export async function pickQuestionForLevel(input: {
  level: number;
  categoryId?: string;
  excludeIds?: string[];
}): Promise<PublicQuestion> {
  if (input.level < 1 || input.level > MAX_LEVEL) {
    throw new BadRequestError(`Level must be 1..${MAX_LEVEL}`);
  }
  const difficulty = difficultyForLevel(input.level);
  const where: Parameters<typeof prisma.question.findFirst>[0] = {
    where: {
      isApproved: true,
      difficulty,
      ...(input.categoryId ? { categoryId: input.categoryId } : {}),
      ...(input.excludeIds?.length ? { id: { notIn: input.excludeIds } } : {}),
    },
  };
  // Use skip = random offset for variety (small pool is fine for MVP)
  const count = await prisma.question.count({ where: where?.where });
  if (count === 0) throw new NotFoundError('No questions available for this level');
  const skip = Math.floor(Math.random() * count);
  const q = await prisma.question.findFirst({
    ...where,
    skip,
    include: { category: { select: { name: true } } },
  });
  if (!q) throw new NotFoundError('No question found');
  return toPublic(q);
}

export async function getQuestionById(id: string, revealAnswer = false) {
  const q = await prisma.question.findUnique({
    where: { id },
    include: { category: { select: { name: true } } },
  });
  if (!q) throw new NotFoundError('Question not found');
  return {
    ...toPublic(q),
    correctOption: revealAnswer ? q.correctOption : undefined,
    explanation: revealAnswer ? q.explanation : undefined,
  };
}
