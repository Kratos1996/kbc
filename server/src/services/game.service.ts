import { prisma } from '../config/db';
import { pickQuestionForLevel, getQuestionById } from './question.service';
import {
  PRIZE_LADDER,
  guaranteedWinnings,
  prizeForLevel,
  isSafeZone,
  MAX_LEVEL,
} from '../utils/prize-ladder';
import { BadRequestError, NotFoundError, ConflictError } from '../utils/errors';

export type GameMode = 'classic' | 'quick' | 'category' | 'practice';
export type GameStatus = 'in_progress' | 'won' | 'lost' | 'quit';

export interface StartGameInput {
  userId: string;
  mode: GameMode;
  categoryId?: string;
  totalQuestions?: number; // for quick mode
}

export async function startGame(input: StartGameInput) {
  const total = input.mode === 'classic' ? MAX_LEVEL : input.totalQuestions ?? 10;
  const game = await prisma.game.create({
    data: {
      userId: input.userId,
      mode: input.mode,
      currentLevel: 0,
      status: 'in_progress',
      categoryId: input.categoryId ?? null,
    },
  });
  const q = await pickQuestionForLevel({ level: 1, categoryId: input.categoryId });
  return { gameId: game.id, total, level: 1, prize: prizeForLevel(1), safeZone: isSafeZone(1), question: q };
}

export async function getCurrentQuestion(gameId: string, userId: string) {
  const game = await prisma.game.findFirst({ where: { id: gameId, userId } });
  if (!game) throw new NotFoundError('Game not found');
  if (game.status !== 'in_progress') throw new ConflictError('Game is not in progress');
  const next = game.currentLevel + 1;
  if (next > MAX_LEVEL) throw new BadRequestError('Game already completed');
  // If a GameAnswer row exists for this level, the user already answered; return the next.
  const answered = await prisma.gameAnswer.findFirst({
    where: { gameId, questionId: { not: undefined } },
    orderBy: { createdAt: 'desc' },
  });
  // Simpler: track which question was last served via a per-game log if needed. For MVP we just re-pick excluding seen ids.
  const seen = await prisma.gameAnswer.findMany({ where: { gameId }, select: { questionId: true } });
  const excludeIds = seen.map((s) => s.questionId);
  const q = await pickQuestionForLevel({ level: next, categoryId: game.categoryId ?? undefined, excludeIds });
  return {
    gameId,
    level: next,
    prize: prizeForLevel(next),
    safeZone: isSafeZone(next),
    question: q,
    // For caller convenience
    _answered: answered ? answered.questionId : null,
  };
}

export async function answerQuestion(input: {
  userId: string;
  gameId: string;
  questionId: string;
  chosenOption: number; // 0..3 or -1 for timeout
  timeMs: number;
}) {
  const game = await prisma.game.findFirst({ where: { id: input.gameId, userId: input.userId } });
  if (!game) throw new NotFoundError('Game not found');
  if (game.status !== 'in_progress') throw new ConflictError('Game is not in progress');

  const question = await prisma.question.findUnique({ where: { id: input.questionId } });
  if (!question) throw new NotFoundError('Question not found');

  const isCorrect = question.correctOption === input.chosenOption;
  await prisma.gameAnswer.create({
    data: {
      gameId: game.id,
      questionId: question.id,
      chosenOption: input.chosenOption,
      isCorrect,
      timeMs: input.timeMs,
    },
  });

  if (!isCorrect) {
    const finalScore = guaranteedWinnings(game.currentLevel + 1);
    await prisma.game.update({
      where: { id: game.id },
      data: { status: 'lost', score: finalScore, endedAt: new Date() },
    });
    return {
      correct: false,
      correctOption: question.correctOption,
      explanation: question.explanation,
      finalScore,
      gameStatus: 'lost' as const,
    };
  }

  // Correct: advance
  const newLevel = game.currentLevel + 1;
  if (newLevel >= MAX_LEVEL) {
    await prisma.game.update({
      where: { id: game.id },
      data: { status: 'won', score: PRIZE_LADDER[MAX_LEVEL - 1], currentLevel: newLevel, endedAt: new Date() },
    });
    return { correct: true, gameStatus: 'won' as const, finalScore: PRIZE_LADDER[MAX_LEVEL - 1] };
  }
  await prisma.game.update({
    where: { id: game.id },
    data: { currentLevel: newLevel, score: PRIZE_LADDER[newLevel - 1] },
  });
  return { correct: true, gameStatus: 'in_progress' as const, score: PRIZE_LADDER[newLevel - 1] };
}

export async function quitGame(userId: string, gameId: string) {
  const game = await prisma.game.findFirst({ where: { id: gameId, userId } });
  if (!game) throw new NotFoundError('Game not found');
  const finalScore = game.currentLevel > 0 ? PRIZE_LADDER[game.currentLevel - 1] : 0;
  await prisma.game.update({
    where: { id: game.id },
    data: { status: 'quit', score: finalScore, endedAt: new Date() },
  });
  return { finalScore };
}

type LifelineType = 'fifty_fifty' | 'audience' | 'phone' | 'expert' | 'flip';

const LIFELINES: LifelineType[] = ['fifty_fifty', 'audience', 'phone', 'expert', 'flip'];

function pickCorrect(question: { correctOption: number }, rng: () => number = Math.random) {
  return question.correctOption;
}

function pickWrongExcluding(correct: number, count: number, rng: () => number = Math.random) {
  const wrong = [0, 1, 2, 3].filter((i) => i !== correct);
  for (let i = wrong.length - 1; i > 0; i--) {
    const j = Math.floor(rng() * (i + 1));
    [wrong[i], wrong[j]] = [wrong[j], wrong[i]];
  }
  return wrong.slice(0, count);
}

export async function useLifeline(input: {
  userId: string;
  gameId: string;
  type: LifelineType;
  questionId: string;
}) {
  if (!LIFELINES.includes(input.type)) throw new BadRequestError('Unknown lifeline type');
  const game = await prisma.game.findFirst({ where: { id: input.gameId, userId: input.userId } });
  if (!game) throw new NotFoundError('Game not found');
  if (game.status !== 'in_progress') throw new ConflictError('Game is not in progress');

  // Idempotency: if the user already used this lifeline, return the cached payload
  const existing = await prisma.lifelineUse.findFirst({
    where: { gameId: game.id, type: input.type },
    orderBy: { createdAt: 'desc' },
  });
  if (existing) return existing.payload as Record<string, unknown>;

  const question = await prisma.question.findUnique({ where: { id: input.questionId } });
  if (!question) throw new NotFoundError('Question not found');

  const correct = question.correctOption;
  const payload: Record<string, unknown> = {};

  switch (input.type) {
    case 'fifty_fifty': {
      const eliminated = pickWrongExcluding(correct, 2);
      payload.eliminatedOptions = eliminated;
      break;
    }
    case 'audience': {
      // Audience: correct gets 60-78%, others share remainder weighted by difficulty
      const base = 60 + Math.floor(Math.random() * 19);
      const remaining = 100 - base;
      const wrong = pickWrongExcluding(correct, 3);
      // Split remainder unevenly: 40/35/25
      const weights = [0.4, 0.35, 0.25];
      const poll: Record<string, number> = {};
      let assigned = 0;
      wrong.forEach((opt, i) => {
        const v = i === wrong.length - 1 ? remaining - assigned : Math.round(remaining * weights[i]);
        poll[opt] = v;
        assigned += v;
      });
      poll[correct] = base;
      payload.poll = poll;
      break;
    }
    case 'phone': {
      payload.suggested = correct;
      break;
    }
    case 'expert': {
      payload.suggested = correct;
      break;
    }
    case 'flip': {
      // Return a fresh question for the same level, not previously seen in this game
      const seen = await prisma.gameAnswer.findMany({ where: { gameId: game.id }, select: { questionId: true } });
      const excludeIds = [...seen.map((s) => s.questionId), question.id];
      const replacement = await pickQuestionForLevel({ level: game.currentLevel + 1, categoryId: game.categoryId ?? undefined, excludeIds });
      payload.question = {
        id: replacement.id,
        text: replacement.text,
        options: replacement.options,
        difficulty: replacement.difficulty,
        categoryId: replacement.categoryId,
        categoryName: replacement.categoryName,
      };
      break;
    }
  }

  await prisma.lifelineUse.create({
    data: { gameId: game.id, type: input.type, payload: payload as object },
  });

  return payload;
}
