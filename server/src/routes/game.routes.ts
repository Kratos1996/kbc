import { Router } from 'express';
import { asyncHandler } from '../utils/async-handler';
import { validate } from '../middleware/validate.middleware';
import { requireAuth, AuthedRequest } from '../middleware/auth.middleware';
import { z } from 'zod';
import * as gameService from '../services/game.service';

export const gameRouter = Router();

const StartSchema = z.object({
  mode: z.enum(['classic', 'quick', 'category', 'practice']),
  categoryId: z.string().optional(),
  totalQuestions: z.number().int().min(1).max(15).optional(),
});

const AnswerSchema = z.object({
  questionId: z.string(),
  chosenOption: z.number().int().min(-1).max(3),
  timeMs: z.number().int().min(0).max(120_000),
});

const LifelineSchema = z.object({
  type: z.enum(['fifty_fifty', 'audience', 'phone', 'expert', 'flip']),
  questionId: z.string(),
});

gameRouter.post(
  '/',
  requireAuth,
  validate(StartSchema),
  asyncHandler(async (req: AuthedRequest, res) => {
    const result = await gameService.startGame({ userId: req.user.sub, ...req.body });
    res.status(201).json(result);
  }),
);

gameRouter.get(
  '/:id/question',
  requireAuth,
  asyncHandler(async (req: AuthedRequest, res) => {
    const result = await gameService.getCurrentQuestion(req.params.id, req.user.sub);
    res.json(result);
  }),
);

gameRouter.post(
  '/:id/answer',
  requireAuth,
  validate(AnswerSchema),
  asyncHandler(async (req: AuthedRequest, res) => {
    const result = await gameService.answerQuestion({ userId: req.user.sub, gameId: req.params.id, ...req.body });
    res.json(result);
  }),
);

gameRouter.post(
  '/:id/quit',
  requireAuth,
  asyncHandler(async (req: AuthedRequest, res) => {
    const result = await gameService.quitGame(req.user.sub, req.params.id);
    res.json(result);
  }),
);

gameRouter.post(
  '/:id/lifeline',
  requireAuth,
  validate(LifelineSchema),
  asyncHandler(async (req: AuthedRequest, res) => {
    const result = await gameService.useLifeline({
      userId: req.user.sub,
      gameId: req.params.id,
      type: req.body.type,
      questionId: req.body.questionId,
    });
    res.json(result);
  }),
);
