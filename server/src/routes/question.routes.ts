import { Router } from 'express';
import { asyncHandler } from '../utils/async-handler';
import { validate } from '../middleware/validate.middleware';
import { requireAuth } from '../middleware/auth.middleware';
import { z } from 'zod';
import { pickQuestionForLevel, getQuestionById } from '../services/question.service';

export const questionRouter = Router();

const PickQuery = z.object({
  level: z.coerce.number().int().min(1).max(15),
  categoryId: z.string().optional(),
});

questionRouter.get(
  '/next',
  requireAuth,
  validate(PickQuery, 'query'),
  asyncHandler(async (req, res) => {
    const { level, categoryId } = req.query as unknown as z.infer<typeof PickQuery>;
    const q = await pickQuestionForLevel({ level, categoryId });
    res.json(q);
  }),
);

questionRouter.get(
  '/:id',
  requireAuth,
  asyncHandler(async (req, res) => {
    const q = await getQuestionById(req.params.id, req.query.reveal === 'true');
    res.json(q);
  }),
);
