import { Router } from 'express';
import { asyncHandler } from '../utils/async-handler';
import { validate } from '../middleware/validate.middleware';
import { requireAuth, AuthedRequest } from '../middleware/auth.middleware';
import { z } from 'zod';
import * as dailyService from '../services/daily.service';

export const dailyRouter = Router();

const SubmitSchema = z.object({
  dailyId: z.string(),
  answers: z
    .array(z.object({ questionId: z.string(), chosenOption: z.number().int().min(0).max(3) }))
    .min(1)
    .max(15),
});

dailyRouter.get(
  '/today',
  requireAuth,
  asyncHandler(async (_req, res) => {
    const daily = await dailyService.getOrCreateTodayDaily();
    res.json(dailyService.publicDaily(daily));
  }),
);

dailyRouter.post(
  '/submit',
  requireAuth,
  validate(SubmitSchema),
  asyncHandler(async (req: AuthedRequest, res) => {
    const result = await dailyService.submitDaily({ userId: req.user.sub, ...req.body });
    res.json(result);
  }),
);

dailyRouter.get(
  '/submissions/today',
  requireAuth,
  asyncHandler(async (req: AuthedRequest, res) => {
    const sub = await dailyService.getTodaySubmission(req.user.sub);
    res.json(sub);
  }),
);
