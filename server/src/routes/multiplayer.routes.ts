import { Router } from 'express';
import { asyncHandler } from '../utils/async-handler';
import { validate } from '../middleware/validate.middleware';
import { requireAuth, AuthedRequest } from '../middleware/auth.middleware';
import { prisma } from '../config/db';
import { BadRequestError, NotFoundError } from '../utils/errors';
import { v4 as uuidv4 } from 'uuid';
import { z } from 'zod';

export const multiplayerRouter = Router();

const CreateAsyncSchema = z.object({ categoryId: z.string().optional() });
const JoinAsyncSchema = z.object({ code: z.string().min(4).max(16) });

multiplayerRouter.post(
  '/async/create',
  requireAuth,
  validate(CreateAsyncSchema),
  asyncHandler(async (req: AuthedRequest, res) => {
    const code = uuidv4().slice(0, 8).toUpperCase();
    const match = await prisma.multiplayerMatch.create({
      data: { mode: 'async', status: 'waiting', inviteCode: code, players: { create: { userId: req.user.sub } } },
    });
    res.status(201).json({ matchId: match.id, code: match.inviteCode });
  }),
);

multiplayerRouter.post(
  '/async/join',
  requireAuth,
  validate(JoinAsyncSchema),
  asyncHandler(async (req: AuthedRequest, res) => {
    const code = req.body.code.toUpperCase();
    const match = await prisma.multiplayerMatch.findUnique({
      where: { inviteCode: code },
      include: { players: { include: { user: { select: { id: true, username: true, displayName: true } } } } },
    });
    if (!match) throw new NotFoundError('No match with that invite code');
    if (match.status === 'finished' || match.status === 'cancelled') {
      throw new BadRequestError('Match has already ended');
    }
    const alreadyIn = match.players.some((p) => p.userId === req.user.sub);
    if (!alreadyIn) {
      await prisma.multiplayerPlayer.create({ data: { matchId: match.id, userId: req.user.sub } });
    }
    const refreshed = await prisma.multiplayerMatch.findUnique({
      where: { id: match.id },
      include: { players: { include: { user: { select: { id: true, username: true, displayName: true } } } } },
    });
    res.json(refreshed);
  }),
);

multiplayerRouter.get(
  '/:id',
  requireAuth,
  asyncHandler(async (req, res) => {
    const match = await prisma.multiplayerMatch.findUnique({
      where: { id: req.params.id },
      include: { players: { include: { user: { select: { id: true, username: true, displayName: true } } } } },
    });
    if (!match) throw new NotFoundError('Match not found');
    res.json(match);
  }),
);
