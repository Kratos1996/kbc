import cron from 'node-cron';
import { startOfIstDay } from '../utils/ist';
import { prisma } from '../config/db';
import { pickQuestionForLevel } from '../services/question.service';
import { logger } from '../config/logger';

const DAILY_QUESTION_COUNT = 10;

// Run at 00:05 IST every day (Node-cron runs in server TZ; if container is UTC, schedule with TZ)
export function startDailyChallengeJob() {
  cron.schedule(
    '5 0 * * *',
    async () => {
      try {
        const today = startOfIstDay();
        const exists = await prisma.dailyChallenge.findUnique({ where: { date: today } });
        if (exists) {
          logger.info({ date: today }, 'daily: already exists, skipping');
          return;
        }
        const ids: string[] = [];
        for (let level = 1; level <= 15 && ids.length < DAILY_QUESTION_COUNT; level++) {
          try { ids.push((await pickQuestionForLevel({ level })).id); } catch { /* empty bucket */ }
        }
        if (ids.length < DAILY_QUESTION_COUNT) {
          logger.warn({ count: ids.length }, 'daily: insufficient questions');
          return;
        }
        await prisma.dailyChallenge.create({
          data: {
            date: today,
            bonusCoins: 100,
            questions: { create: ids.map((qid, i) => ({ questionId: qid, orderIndex: i })) },
          },
        });
        logger.info({ date: today, count: ids.length }, 'daily: created');
      } catch (err) {
        logger.error({ err }, 'daily: job failed');
      }
    },
    { timezone: 'Asia/Kolkata' },
  );
  logger.info('Daily challenge job scheduled at 00:05 IST');
}
