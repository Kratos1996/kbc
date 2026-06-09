import http from 'http';
import { createApp } from './app';
import { env } from './config/env';
import { connectDb, disconnectDb } from './config/db';
import { initSocket } from './socket';
import { startDailyChallengeJob } from './jobs/daily-challenge.job';
import { logger } from './config/logger';

async function main() {
  await connectDb();
  const app = createApp();
  const server = http.createServer(app);
  initSocket(server);
  startDailyChallengeJob();

  server.listen(env.PORT, () => {
    logger.info({ port: env.PORT, env: env.NODE_ENV }, 'KBC API listening');
  });

  const shutdown = async (signal: string) => {
    logger.info({ signal }, 'shutting down');
    server.close();
    await disconnectDb();
    process.exit(0);
  };
  process.on('SIGINT', () => shutdown('SIGINT'));
  process.on('SIGTERM', () => shutdown('SIGTERM'));
}

main().catch((err) => {
  // eslint-disable-next-line no-console
  console.error('Fatal startup error', err);
  process.exit(1);
});
