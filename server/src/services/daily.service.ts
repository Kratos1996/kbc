import { prisma } from '../config/db';
import { pickQuestionForLevel } from './question.service';
import { NotFoundError } from '../utils/errors';

const DAILY_QUESTION_COUNT = 10;
const IST_OFFSET_MIN = 330;

function startOfIstDay(d: Date = new Date()): Date {
  const utc = d.getTime();
  const ist = new Date(utc + IST_OFFSET_MIN * 60_000);
  return new Date(Date.UTC(ist.getUTCFullYear(), ist.getUTCMonth(), ist.getUTCDate()) - IST_OFFSET_MIN * 60_000);
}

export async function getOrCreateTodayDaily() {
  const today = startOfIstDay();
  let daily = await prisma.dailyChallenge.findUnique({
    where: { date: today },
    include: {
      questions: { include: { question: { include: { category: { select: { name: true } } } } }, orderBy: { orderIndex: 'asc' } },
    },
  });
  if (!daily) {
    daily = await createDailyFor(today);
  }
  return daily;
}

async function createDailyFor(date: Date) {
  // Pick a mix of difficulties
  const easy: string[] = [];
  const med: string[] = [];
  const hard: string[] = [];
  for (let level = 1; level <= 5; level++) {
    try { easy.push((await pickQuestionForLevel({ level })).id); } catch { /* empty bucket */ }
  }
  for (let level = 6; level <= 10; level++) {
    try { med.push((await pickQuestionForLevel({ level })).id); } catch { /* empty bucket */ }
  }
  for (let level = 11; level <= 15; level++) {
    try { hard.push((await pickQuestionForLevel({ level })).id); } catch { /* empty bucket */ }
  }
  const all = [...easy, ...med, ...hard].slice(0, DAILY_QUESTION_COUNT);
  if (all.length < DAILY_QUESTION_COUNT) {
    throw new NotFoundError('Not enough questions to build the daily challenge');
  }
  return prisma.dailyChallenge.create({
    data: {
      date,
      bonusCoins: 100,
      questions: { create: all.map((qid, i) => ({ questionId: qid, orderIndex: i })) },
    },
    include: {
      questions: { include: { question: { include: { category: { select: { name: true } } } } }, orderBy: { orderIndex: 'asc' } },
    },
  });
}

export function publicDaily(daily: NonNullable<Awaited<ReturnType<typeof getOrCreateTodayDaily>>>) {
  return {
    id: daily.id,
    date: daily.date,
    bonusCoins: daily.bonusCoins,
    questions: daily.questions.map((dq) => ({
      orderIndex: dq.orderIndex,
      id: dq.question.id,
      text: dq.question.text,
      options: [dq.question.optionA, dq.question.optionB, dq.question.optionC, dq.question.optionD],
      difficulty: dq.question.difficulty,
      categoryName: dq.question.category.name,
    })),
  };
}

export async function submitDaily(input: { userId: string; dailyId: string; answers: Array<{ questionId: string; chosenOption: number }> }) {
  const daily = await prisma.dailyChallenge.findUnique({
    where: { id: input.dailyId },
    include: { questions: { include: { question: true } } },
  });
  if (!daily) throw new NotFoundError('Daily challenge not found');
  const map = new Map(daily.questions.map((dq) => [dq.questionId, dq.question]));
  let correct = 0;
  for (const a of input.answers) {
    const q = map.get(a.questionId);
    if (q && q.correctOption === a.chosenOption) correct += 1;
  }
  const score = correct * 100;
  const submission = await prisma.dailySubmission.upsert({
    where: { userId_dailyId: { userId: input.userId, dailyId: input.dailyId } },
    create: { userId: input.userId, dailyId: input.dailyId, score },
    update: { score },
  });
  if (correct === daily.questions.length) {
    await prisma.user.update({
      where: { id: input.userId },
      data: { coins: { increment: daily.bonusCoins } },
    });
  }
  return { score, correct, total: daily.questions.length, bonusAwarded: correct === daily.questions.length };
}

export async function getTodaySubmission(userId: string) {
  const today = startOfIstDay();
  const daily = await prisma.dailyChallenge.findUnique({ where: { date: today } });
  if (!daily) return null;
  const sub = await prisma.dailySubmission.findUnique({
    where: { userId_dailyId: { userId, dailyId: daily.id } },
  });
  if (!sub) return null;
  return {
    dailyId: daily.id,
    score: sub.score,
    submittedAt: sub.createdAt,
  };
}
