import { Router } from 'express';
import { asyncHandler } from '../utils/async-handler';
import { requireAuth, AuthedRequest } from '../middleware/auth.middleware';
import { prisma } from '../config/db';
import { NotFoundError } from '../utils/errors';

export const userRouter = Router();

userRouter.get(
  '/me',
  requireAuth,
  asyncHandler(async (req: AuthedRequest, res) => {
    const u = await prisma.user.findUnique({
      where: { id: req.user.sub },
      select: {
        id: true, email: true, username: true, displayName: true, avatarUrl: true,
        coins: true, isPremium: true, premiumUntil: true, createdAt: true,
      },
    });
    if (!u) throw new NotFoundError('User not found');
    res.json(u);
  }),
);

userRouter.patch(
  '/me',
  requireAuth,
  asyncHandler(async (req: AuthedRequest, res) => {
    const { displayName, avatarUrl } = req.body as { displayName?: string; avatarUrl?: string };
    const u = await prisma.user.update({
      where: { id: req.user.sub },
      data: {
        ...(typeof displayName === 'string' ? { displayName } : {}),
        ...(typeof avatarUrl === 'string' ? { avatarUrl } : {}),
      },
      select: { id: true, displayName: true, avatarUrl: true },
    });
    res.json(u);
  }),
);

userRouter.get(
  '/me/stats',
  requireAuth,
  asyncHandler(async (req: AuthedRequest, res) => {
    const userId = req.user.sub;
    const [played, won, bestAgg, totalAgg] = await Promise.all([
      prisma.game.count({ where: { userId, status: { in: ['won', 'lost', 'quit'] } } }),
      prisma.game.count({ where: { userId, status: 'won' } }),
      prisma.game.aggregate({ where: { userId }, _max: { score: true } }),
      prisma.game.aggregate({ where: { userId }, _sum: { score: true } }),
    ]);
    res.json({
      gamesPlayed: played,
      gamesWon: won,
      bestScore: bestAgg._max.score ?? 0,
      totalScore: totalAgg._sum.score ?? 0,
    });
  }),
);
