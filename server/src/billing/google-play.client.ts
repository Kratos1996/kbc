import { google } from 'googleapis';
import { env } from '../config/env';
import fs from 'fs';

interface AndroidPublisher {
  purchases: {
    products: { get: (params: { packageName: string; productId: string; token: string }) => Promise<{ data: unknown }> };
    subscriptionsv2: { get: (params: { packageName: string; token: string }) => Promise<{ data: unknown }> };
  };
}

let cachedClient: AndroidPublisher | null = null;

async function client(): Promise<AndroidPublisher> {
  if (cachedClient) return cachedClient;
  if (!env.GOOGLE_PLAY_SERVICE_ACCOUNT_JSON) {
    throw new Error('GOOGLE_PLAY_SERVICE_ACCOUNT_JSON not configured');
  }
  const keyFile = env.GOOGLE_PLAY_SERVICE_ACCOUNT_JSON;
  if (!fs.existsSync(keyFile)) {
    throw new Error(`Service account JSON not found at ${keyFile}`);
  }
  const auth = new google.auth.GoogleAuth({
    keyFile,
    scopes: ['https://www.googleapis.com/auth/androidpublisher'],
  });
  const authClient = await auth.getClient();
  // google.androidpublisher v3 types are partial; cast to our narrow interface.
  const publisher = google.androidpublisher({
    version: 'v3',
    auth: authClient as never,
  });
  cachedClient = publisher as unknown as AndroidPublisher;
  return cachedClient;
}

export async function verifyProductPurchase(input: { productId: string; purchaseToken: string }) {
  const c = await client();
  const res = await c.purchases.products.get({
    packageName: env.GOOGLE_PLAY_PACKAGE_NAME,
    productId: input.productId,
    token: input.purchaseToken,
  });
  return res.data as Record<string, unknown>;
}

export async function verifySubscriptionPurchase(input: { purchaseToken: string }) {
  const c = await client();
  const res = await c.purchases.subscriptionsv2.get({
    packageName: env.GOOGLE_PLAY_PACKAGE_NAME,
    token: input.purchaseToken,
  });
  return res.data as Record<string, unknown>;
}
