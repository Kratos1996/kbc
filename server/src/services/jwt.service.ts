import jwt, { SignOptions } from 'jsonwebtoken';
import { env } from '../config/env';
import { v4 as uuidv4 } from 'uuid';

export interface TokenPayload {
  sub: string; // user id
  username: string;
  jti: string;
}

export interface AccessTokenPayload extends TokenPayload {
  type: 'access';
}

export interface RefreshTokenPayload extends TokenPayload {
  type: 'refresh';
}

export function signAccessToken(userId: string, username: string): string {
  const payload: Omit<AccessTokenPayload, 'type'> = { sub: userId, username, jti: uuidv4() };
  return jwt.sign(payload, env.JWT_ACCESS_SECRET, {
    expiresIn: env.JWT_ACCESS_TTL,
  } as SignOptions);
}

export function signRefreshToken(userId: string, username: string): string {
  const payload: Omit<RefreshTokenPayload, 'type'> = { sub: userId, username, jti: uuidv4() };
  return jwt.sign(payload, env.JWT_REFRESH_SECRET, {
    expiresIn: env.JWT_REFRESH_TTL,
  } as SignOptions);
}

export function verifyAccessToken(token: string): TokenPayload {
  const decoded = jwt.verify(token, env.JWT_ACCESS_SECRET) as AccessTokenPayload;
  return { sub: decoded.sub, username: decoded.username, jti: decoded.jti };
}

export function verifyRefreshToken(token: string): TokenPayload {
  const decoded = jwt.verify(token, env.JWT_REFRESH_SECRET) as RefreshTokenPayload;
  return { sub: decoded.sub, username: decoded.username, jti: decoded.jti };
}

export function refreshTtlMs(): number {
  // Parse e.g. "30d" / "15m" / "1h"
  const m = env.JWT_REFRESH_TTL.match(/^(\d+)([smhd])$/);
  if (!m) return 30 * 24 * 60 * 60 * 1000;
  const n = parseInt(m[1], 10);
  const unit = m[2];
  const mult: Record<string, number> = { s: 1_000, m: 60_000, h: 3_600_000, d: 86_400_000 };
  return n * (mult[unit] ?? 86_400_000);
}
