import { Request, Response, NextFunction } from 'express';
import { verifyAccessToken, TokenPayload } from '../services/jwt.service';
import { UnauthorizedError } from '../utils/errors';

export type AuthedRequest = Request & { user: TokenPayload };

export const requireAuth = (req: Request, _res: Response, next: NextFunction) => {
  const header = req.headers.authorization;
  if (!header || !header.toLowerCase().startsWith('bearer ')) {
    return next(new UnauthorizedError('Missing bearer token'));
  }
  const token = header.slice(7).trim();
  try {
    const payload = verifyAccessToken(token);
    (req as AuthedRequest).user = payload;
    next();
  } catch {
    next(new UnauthorizedError('Invalid or expired token'));
  }
};

export const optionalAuth = (req: Request, _res: Response, next: NextFunction) => {
  const header = req.headers.authorization;
  if (!header || !header.toLowerCase().startsWith('bearer ')) return next();
  const token = header.slice(7).trim();
  try {
    const payload = verifyAccessToken(token);
    (req as AuthedRequest).user = payload;
  } catch {
    /* ignore */
  }
  next();
};
