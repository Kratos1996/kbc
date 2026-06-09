import { Router } from 'express';
import { authRouter } from './auth.routes';
import { categoryRouter } from './category.routes';
import { questionRouter } from './question.routes';
import { gameRouter } from './game.routes';
import { dailyRouter } from './daily.routes';
import { leaderboardRouter } from './leaderboard.routes';
import { userRouter } from './user.routes';
import { billingRouter } from './billing.routes';
import { multiplayerRouter } from './multiplayer.routes';
import { asyncHandler } from '../utils/async-handler';
import { listCategories } from '../services/question.service';

export const apiRouter = Router();

apiRouter.get('/health', (_req, res) => res.json({ ok: true, ts: new Date().toISOString() }));
apiRouter.get('/meta/categories', asyncHandler(async (_req, res) => res.json(await listCategories())));

apiRouter.use('/auth', authRouter);
apiRouter.use('/categories', categoryRouter);
apiRouter.use('/questions', questionRouter);
apiRouter.use('/games', gameRouter);
apiRouter.use('/daily', dailyRouter);
apiRouter.use('/leaderboard', leaderboardRouter);
apiRouter.use('/users', userRouter);
apiRouter.use('/billing', billingRouter);
apiRouter.use('/multiplayer', multiplayerRouter);
