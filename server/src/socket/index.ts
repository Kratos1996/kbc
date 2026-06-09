import { Server as SocketServer } from 'socket.io';
import { env } from '../config/env';
import { attachMultiplayer } from './multiplayer.handler';
import { logger } from '../config/logger';

let io: SocketServer | null = null;

export function initSocket(httpServer: import('http').Server): SocketServer {
  io = new SocketServer(httpServer, {
    cors: { origin: env.corsOrigins === '*' ? true : env.corsOrigins, credentials: true },
  });
  attachMultiplayer(io);
  logger.info('Socket.IO initialized');
  return io;
}

export function getIo(): SocketServer {
  if (!io) throw new Error('Socket.IO not initialized');
  return io;
}
