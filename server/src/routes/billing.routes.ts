import { Router } from 'express';
import { asyncHandler } from '../utils/async-handler';
import { validate } from '../middleware/validate.middleware';
import { requireAuth, AuthedRequest } from '../middleware/auth.middleware';
import { z } from 'zod';
import * as billingService from '../services/billing.service';

export const billingRouter = Router();

const VerifySchema = z.object({
  productId: z.string().min(1),
  purchaseToken: z.string().min(1),
});

billingRouter.get(
  '/products',
  requireAuth,
  asyncHandler(async (_req, res) => {
    res.json(await billingService.listProducts());
  }),
);

billingRouter.post(
  '/verify',
  requireAuth,
  validate(VerifySchema),
  asyncHandler(async (req: AuthedRequest, res) => {
    const result = await billingService.verifyAndGrant({
      userId: req.user.sub,
      productId: req.body.productId,
      purchaseToken: req.body.purchaseToken,
    });
    res.json(result);
  }),
);
