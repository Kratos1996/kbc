import bcrypt from 'bcryptjs';
import crypto from 'crypto';
import { prisma } from '../config/db';
import { signAccessToken, signRefreshToken, verifyRefreshToken, refreshTtlMs } from './jwt.service';
import { ConflictError, UnauthorizedError } from '../utils/errors';

const SALT_ROUNDS = 12;
const REFRESH_BYTES = 48;

export interface AuthResult {
  user: {
    id: string;
    email: string;
    username: string;
    displayName: string | null;
    avatarUrl: string | null;
    coins: number;
    isPremium: boolean;
    premiumUntil: string | null;
  };
  accessToken: string;
  refreshToken: string;
}

function hashRefresh(token: string): string {
  return crypto.createHash('sha256').update(token).digest('hex');
}

function toAuthUser(u: {
  id: string;
  email: string;
  username: string;
  displayName: string | null;
  avatarUrl: string | null;
  coins: number;
  isPremium: boolean;
  premiumUntil: Date | null;
}) {
  return {
    id: u.id,
    email: u.email,
    username: u.username,
    displayName: u.displayName,
    avatarUrl: u.avatarUrl,
    coins: u.coins,
    isPremium: u.isPremium,
    premiumUntil: u.premiumUntil?.toISOString() ?? null,
  };
}

export async function register(input: {
  email: string;
  username: string;
  password: string;
  displayName?: string;
}): Promise<AuthResult> {
  const existing = await prisma.user.findFirst({
    where: { OR: [{ email: input.email }, { username: input.username }] },
  });
  if (existing) {
    throw new ConflictError('Email or username already in use');
  }
  const passwordHash = await bcrypt.hash(input.password, SALT_ROUNDS);
  const user = await prisma.user.create({
    data: {
      email: input.email,
      username: input.username,
      passwordHash,
      displayName: input.displayName ?? null,
    },
  });
  return issueTokens(user);
}

export async function login(input: { emailOrUsername: string; password: string }): Promise<AuthResult> {
  const user = await prisma.user.findFirst({
    where: {
      OR: [{ email: input.emailOrUsername }, { username: input.emailOrUsername }],
    },
  });
  if (!user) throw new UnauthorizedError('Invalid credentials');
  const ok = await bcrypt.compare(input.password, user.passwordHash);
  if (!ok) throw new UnauthorizedError('Invalid credentials');
  return issueTokens(user);
}

export async function refresh(refreshToken: string): Promise<AuthResult> {
  let payload;
  try {
    payload = verifyRefreshToken(refreshToken);
  } catch {
    throw new UnauthorizedError('Invalid refresh token');
  }
  const tokenHash = hashRefresh(refreshToken);
  const stored = await prisma.refreshToken.findUnique({ where: { tokenHash } });
  if (!stored || stored.revoked || stored.expiresAt < new Date()) {
    throw new UnauthorizedError('Refresh token revoked or expired');
  }
  const user = await prisma.user.findUnique({ where: { id: payload.sub } });
  if (!user) throw new UnauthorizedError('User not found');

  // Rotate: revoke old, issue new
  await prisma.refreshToken.update({ where: { id: stored.id }, data: { revoked: true } });
  return issueTokens(user);
}

export async function logout(refreshToken: string): Promise<void> {
  const tokenHash = hashRefresh(refreshToken);
  await prisma.refreshToken.updateMany({ where: { tokenHash }, data: { revoked: true } });
}

async function issueTokens(user: {
  id: string;
  email: string;
  username: string;
  displayName: string | null;
  avatarUrl: string | null;
  coins: number;
  isPremium: boolean;
  premiumUntil: Date | null;
}): Promise<AuthResult> {
  const accessToken = signAccessToken(user.id, user.username);
  const refreshToken = signRefreshToken(user.id, user.username);
  const expiresAt = new Date(Date.now() + refreshTtlMs());
  await prisma.refreshToken.create({
    data: { userId: user.id, tokenHash: hashRefresh(refreshToken), expiresAt },
  });
  return { user: toAuthUser(user), accessToken, refreshToken };
}

export async function getMe(userId: string) {
  const user = await prisma.user.findUnique({ where: { id: userId } });
  if (!user) throw new UnauthorizedError('User not found');
  return toAuthUser(user);
}
