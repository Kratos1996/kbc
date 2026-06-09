import { prisma } from '../config/db';
import { dateFns } from '../utils/date-fns';
import { BadRequestError } from '../utils/errors';

export type LeaderboardScope = 'global' | 'weekly' | 'monthly' | 'friends';

export async function getLeaderboard(input: { scope: LeaderboardScope; userId?: string; limit?: number }) {
  const limit = Math.min(Math.max(input.limit ?? 50, 1), 200);
  const period = computePeriod(input.scope);

  if (input.scope === 'friends') {
    if (!input.userId) throw new BadRequestError('userId required for friends leaderboard');
    const friends = await prisma.friendship.findMany({
      where: {
        status: 'accepted',
        OR: [{ requesterId: input.userId }, { addresseeId: input.userId }],
      },
    });
    const friendIds = friends.map((f) => (f.requesterId === input.userId ? f.addresseeId : f.requesterId));
    friendIds.push(input.userId);
    return topUsers({ userIds: friendIds, scope: 'friends', period, limit });
  }

  return topUsers({ userIds: undefined, scope: input.scope, period, limit });
}

function computePeriod(scope: LeaderboardScope): string {
  const now = new Date();
  switch (scope) {
    case 'weekly':
      return isoWeek(now);
    case 'monthly':
      return `${now.getUTCFullYear()}-${String(now.getUTCMonth() + 1).padStart(2, '0')}`;
    case 'global':
    case 'friends':
    default:
      return 'all-time';
  }
}

function isoWeek(d: Date): string {
  const target = new Date(Date.UTC(d.getUTCFullYear(), d.getUTCMonth(), d.getUTCDate()));
  const dayNr = (target.getUTCDay() + 6) % 7;
  target.setUTCDate(target.getUTCDate() - dayNr + 3);
  const firstThursday = new Date(Date.UTC(target.getUTCFullYear(), 0, 4));
  const diff = (target.getTime() - firstThursday.getTime()) / 86_400_000;
  const week = 1 + Math.round((diff - 3 + ((firstThursday.getUTCDay() + 6) % 7)) / 7);
  return `${target.getUTCFullYear()}-W${String(week).padStart(2, '0')}`;
}

async function topUsers(input: { userIds?: string[]; scope: string; period: string; limit: number }) {
  // Aggregate from Game table (source of truth) for the current period
  const now = new Date();
  const since =
    input.period === 'all-time'
      ? undefined
      : input.scope === 'weekly'
        ? new Date(now.getTime() - 7 * 86_400_000)
        : new Date(Date.UTC(now.getUTCFullYear(), now.getUTCMonth(), 1));

  const games = await prisma.game.groupBy({
    by: ['userId'],
    where: {
      status: { in: ['won', 'lost', 'quit'] },
      ...(since ? { endedAt: { gte: since } } : {}),
      ...(input.userIds ? { userId: { in: input.userIds } } : {}),
    },
    _sum: { score: true },
    _max: { score: true },
    orderBy: { _sum: { score: 'desc' } },
    take: input.limit,
  });

  const userIds = games.map((g) => g.userId);
  const users = await prisma.user.findMany({
    where: { id: { in: userIds } },
    select: { id: true, username: true, displayName: true, avatarUrl: true },
  });
  const uMap = new Map(users.map((u) => [u.id, u]));
  return games.map((g, i) => ({
    rank: i + 1,
    userId: g.userId,
    username: uMap.get(g.userId)?.username ?? 'unknown',
    displayName: uMap.get(g.userId)?.displayName ?? null,
    avatarUrl: uMap.get(g.userId)?.avatarUrl ?? null,
    totalScore: g._sum.score ?? 0,
    bestScore: g._max.score ?? 0,
  }));
}
