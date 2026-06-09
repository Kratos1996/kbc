import { Router } from 'express';
import { asyncHandler } from '../utils/async-handler';
import { validate } from '../middleware/validate.middleware';
import { requireAuth, AuthedRequest, optionalAuth } from '../middleware/auth.middleware';
import { z } from 'zod';
import * as leaderboardService from '../services/leaderboard.service';

export const leaderboardRouter = Router();

const QuerySchema = z.object({
  scope: z.enum(['global', 'weekly', 'monthly', 'friends']).default('global'),
  limit: z.coerce.number().int().min(1).max(200).optional(),
});

leaderboardRouter.get(
  '/',
  optionalAuth,
  validate(QuerySchema, 'query'),
  asyncHandler(async (req: AuthedRequest, res) => {
    const { scope, limit } = req.query as unknown as z.infer<typeof QuerySchema>;
    const userId = req.user?.sub;
    const result = await leaderboardService.getLeaderboard({ scope, userId, limit });
    res.json(result);
  }),
);
