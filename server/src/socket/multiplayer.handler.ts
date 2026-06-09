import { Server as SocketServer, Socket } from 'socket.io';
import { verifyAccessToken } from '../services/jwt.service';
import { logger } from '../config/logger';
import { prisma } from '../config/db';
import { v4 as uuidv4 } from 'uuid';
import { pickQuestionForLevel } from '../services/question.service';

interface Player {
  userId: string;
  username: string;
  socketId: string;
  score: number;
  answers: Map<string, number>; // questionId -> chosenOption
}

interface Room {
  matchId: string;
  players: Map<string, Player>;
  currentLevel: number;
  currentQuestionId: string | null;
  startedAt: Date | null;
  status: 'waiting' | 'in_progress' | 'finished';
}

const rooms = new Map<string, Room>();
const QUESTIONS_PER_MATCH = 10;
const LEVEL_REVEAL_MS = 4_000;
const PER_QUESTION_MS = 20_000;

export function attachMultiplayer(io: SocketServer) {
  io.use((socket, next) => {
    try {
      const token = (socket.handshake.auth?.token as string | undefined) ?? '';
      const payload = verifyAccessToken(token);
      (socket.data as { userId: string; username: string }).userId = payload.sub;
      (socket.data as { userId: string; username: string }).username = payload.username;
      next();
    } catch {
      next(new Error('Unauthorized'));
    }
  });

  io.on('connection', (socket: Socket) => {
    const { userId, username } = socket.data as { userId: string; username: string };
    logger.info({ userId, socketId: socket.id }, 'mp:connected');

    socket.on('mp:create', async (cb: (resp: unknown) => void) => {
      const code = uuidv4().slice(0, 6).toUpperCase();
      const match = await prisma.multiplayerMatch.create({
        data: { mode: 'live_1v1', status: 'waiting', inviteCode: code },
      });
      const room: Room = {
        matchId: match.id,
        players: new Map(),
        currentLevel: 0,
        currentQuestionId: null,
        startedAt: null,
        status: 'waiting',
      };
      room.players.set(userId, { userId, username, socketId: socket.id, score: 0, answers: new Map() });
      await prisma.multiplayerPlayer.create({ data: { matchId: match.id, userId } });
      rooms.set(match.id, room);
      socket.join(match.id);
      cb({ ok: true, matchId: match.id, code });
    });

    socket.on('mp:join', async ({ code }: { code: string }, cb: (resp: unknown) => void) => {
      const match = await prisma.multiplayerMatch.findUnique({ where: { inviteCode: code } });
      if (!match) return cb({ ok: false, error: 'Match not found' });
      const room = rooms.get(match.id);
      if (!room) return cb({ ok: false, error: 'Match expired, please create a new one' });
      if (room.players.size >= 2) return cb({ ok: false, error: 'Match is full' });
      if (room.players.has(userId)) return cb({ ok: false, error: 'Already joined' });
      room.players.set(userId, { userId, username, socketId: socket.id, score: 0, answers: new Map() });
      await prisma.multiplayerPlayer.create({ data: { matchId: match.id, userId } });
      socket.join(match.id);
      cb({ ok: true, matchId: match.id });
      io.to(match.id).emit('mp:players', Array.from(room.players.values()).map((p) => ({ userId: p.userId, username: p.username, score: p.score })));
      if (room.players.size === 2) startMatch(io, room);
    });

    socket.on('mp:answer', async ({ matchId, questionId, chosenOption }: { matchId: string; questionId: string; chosenOption: number }, cb: (resp: unknown) => void) => {
      const room = rooms.get(matchId);
      if (!room) return cb({ ok: false, error: 'No such match' });
      const player = room.players.get(userId);
      if (!player) return cb({ ok: false, error: 'Not in match' });
      if (room.currentQuestionId !== questionId) return cb({ ok: false, error: 'Stale question' });
      if (player.answers.has(questionId)) return cb({ ok: false, error: 'Already answered' });

      const q = await prisma.question.findUnique({ where: { id: questionId } });
      if (!q) return cb({ ok: false, error: 'No question' });
      const correct = q.correctOption === chosenOption;
      player.answers.set(questionId, chosenOption);
      if (correct) player.score += 100;
      io.to(matchId).emit('mp:score', { userId, score: player.score });
      cb({ ok: true, correct });
    });

    socket.on('disconnect', () => {
      for (const room of rooms.values()) {
        if (room.players.get(userId)?.socketId === socket.id) {
          room.players.delete(userId);
          io.to(room.matchId).emit('mp:players', Array.from(room.players.values()).map((p) => ({ userId: p.userId, username: p.username, score: p.score })));
        }
      }
    });
  });
}

async function startMatch(io: SocketServer, room: Room) {
  room.status = 'in_progress';
  room.startedAt = new Date();
  await prisma.multiplayerMatch.update({ where: { id: room.matchId }, data: { status: 'in_progress', startedAt: room.startedAt } });
  io.to(room.matchId).emit('mp:start', { totalQuestions: QUESTIONS_PER_MATCH });

  for (let level = 1; level <= QUESTIONS_PER_MATCH; level++) {
    if (room.players.size < 2) break;
    room.currentLevel = level;
    const q = await pickQuestionForLevel({ level });
    room.currentQuestionId = q.id;
    io.to(room.matchId).emit('mp:question', {
      level,
      questionId: q.id,
      text: q.text,
      options: q.options,
      timeoutMs: PER_QUESTION_MS,
    });
    await sleep(PER_QUESTION_MS + LEVEL_REVEAL_MS);
  }

  // Finish
  const sorted = Array.from(room.players.values()).sort((a, b) => b.score - a.score);
  await prisma.multiplayerMatch.update({
    where: { id: room.matchId },
    data: { status: 'finished', endedAt: new Date() },
  });
  for (const p of room.players.values()) {
    const rank = sorted.findIndex((s) => s.userId === p.userId) + 1;
    await prisma.multiplayerPlayer.updateMany({
      where: { matchId: room.matchId, userId: p.userId },
      data: { score: p.score, rank },
    });
  }
  io.to(room.matchId).emit('mp:end', {
    results: sorted.map((p, i) => ({ userId: p.userId, username: p.username, score: p.score, rank: i + 1 })),
  });
  rooms.delete(room.matchId);
}

function sleep(ms: number) {
  return new Promise((res) => setTimeout(res, ms));
}
