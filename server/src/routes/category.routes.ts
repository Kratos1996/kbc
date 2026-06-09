import { Router } from 'express';
import { asyncHandler } from '../utils/async-handler';
import { listCategories } from '../services/question.service';

export const categoryRouter = Router();

categoryRouter.get(
  '/',
  asyncHandler(async (_req, res) => {
    const cats = await listCategories();
    res.json(cats);
  }),
);
