import { prisma } from '../config/db';
import { verifyProductPurchase, verifySubscriptionPurchase } from '../billing/google-play.client';
import { BadRequestError, NotFoundError } from '../utils/errors';
import { logger } from '../config/logger';

export const PRODUCT_CATALOG: Record<string, { type: 'product' | 'subscription'; coins?: number; tier?: 'monthly' | 'yearly' }> = {
  coins_small: { type: 'product', coins: 100 },
  coins_medium: { type: 'product', coins: 500 },
  coins_large: { type: 'product', coins: 1500 },
  lifeline_restock: { type: 'product', coins: 50 },
  premium_monthly: { type: 'subscription', tier: 'monthly' },
  premium_yearly: { type: 'subscription', tier: 'yearly' },
};

export async function listProducts() {
  return Object.entries(PRODUCT_CATALOG).map(([id, meta]) => ({ id, ...meta }));
}

export async function verifyAndGrant(input: { userId: string; productId: string; purchaseToken: string }) {
  const meta = PRODUCT_CATALOG[input.productId];
  if (!meta) throw new BadRequestError('Unknown productId');

  // Idempotency: same token stored once
  const existing = await prisma.iapReceipt.findUnique({ where: { purchaseToken: input.purchaseToken } });
  if (existing) return { alreadyProcessed: true, productId: input.productId };

  if (meta.type === 'subscription') {
    const data = await verifySubscriptionPurchase({ purchaseToken: input.purchaseToken });
    // Compute expiry from the response (subscriptionV2 shape)
    const lineItems = (data as { subscriptionState?: string; lineItems?: Array<{ expiryTime?: string }> }).lineItems ?? [];
    const expiresAt = lineItems[0]?.expiryTime ? new Date(lineItems[0].expiryTime) : null;
    await prisma.iapReceipt.create({
      data: {
        userId: input.userId,
        productId: input.productId,
        purchaseToken: input.purchaseToken,
        verifiedAt: new Date(),
        expiresAt,
      },
    });
    if (expiresAt) {
      const now = new Date();
      const newPremiumUntil = expiresAt > now ? expiresAt : null;
      await prisma.user.update({
        where: { id: input.userId },
        data: { isPremium: !!newPremiumUntil, premiumUntil: newPremiumUntil },
      });
    }
  } else {
    await verifyProductPurchase({ productId: input.productId, purchaseToken: input.purchaseToken });
    await prisma.iapReceipt.create({
      data: {
        userId: input.userId,
        productId: input.productId,
        purchaseToken: input.purchaseToken,
        verifiedAt: new Date(),
      },
    });
    if (meta.coins) {
      await prisma.user.update({
        where: { id: input.userId },
        data: { coins: { increment: meta.coins } },
      });
    }
  }
  logger.info({ userId: input.userId, productId: input.productId }, 'iap: granted');
  return { alreadyProcessed: false, productId: input.productId };
}
