import { z } from 'zod';

export const RegisterSchema = z.object({
  email: z.string().email().max(120),
  username: z.string().min(3).max(32).regex(/^[a-zA-Z0-9_.]+$/),
  password: z.string().min(8).max(128),
  displayName: z.string().min(1).max(60).optional(),
});

export const LoginSchema = z.object({
  emailOrUsername: z.string().min(3).max(120),
  password: z.string().min(8).max(128),
});

export const RefreshSchema = z.object({
  refreshToken: z.string().min(20),
});
