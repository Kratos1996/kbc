import { Router } from 'express';
import { asyncHandler } from '../utils/async-handler';
import { validate } from '../middleware/validate.middleware';
import { authLimiter } from '../middleware/rate-limit.middleware';
import { RegisterSchema, LoginSchema, RefreshSchema } from '../validators/auth.validator';
import * as authService from '../services/auth.service';
import { requireAuth, AuthedRequest } from '../middleware/auth.middleware';

export const authRouter = Router();

authRouter.post(
  '/register',
  authLimiter,
  validate(RegisterSchema),
  asyncHandler(async (req, res) => {
    const result = await authService.register(req.body);
    res.status(201).json(result);
  }),
);

authRouter.post(
  '/login',
  authLimiter,
  validate(LoginSchema),
  asyncHandler(async (req, res) => {
    const result = await authService.login(req.body);
    res.json(result);
  }),
);

authRouter.post(
  '/refresh',
  validate(RefreshSchema),
  asyncHandler(async (req, res) => {
    const result = await authService.refresh(req.body.refreshToken);
    res.json(result);
  }),
);

authRouter.post(
  '/logout',
  validate(RefreshSchema),
  asyncHandler(async (req, res) => {
    await authService.logout(req.body.refreshToken);
    res.status(204).end();
  }),
);

authRouter.get(
  '/me',
  requireAuth,
  asyncHandler(async (req: AuthedRequest, res) => {
    const user = await authService.getMe(req.user.sub);
    res.json(user);
  }),
);
