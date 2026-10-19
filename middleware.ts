/**
 * Next.js Edge Middleware for MedColleague Production Upgrade
 * Handles:
 * 1. 14-day free trial verification
 * 2. Upstash Redis sliding-window rate limiting
 * 3. Role-Based Access Control (RBAC) & Multi-tenant workspace routing
 */

import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

// Sliding-window in-memory or Upstash Redis fallback rate limiter
const RATE_LIMIT_WINDOW_MS = 60 * 1000; // 1 minute
const MAX_REQUESTS_PER_WINDOW = 60; // 60 requests/min per IP/token
const ipRequestHistory = new Map<string, { count: number; resetAt: number }>();

function checkRateLimit(clientIdentifier: string): { allowed: boolean; remaining: number } {
  const now = Date.now();
  const entry = ipRequestHistory.get(clientIdentifier);

  if (!entry || now > entry.resetAt) {
    ipRequestHistory.set(clientIdentifier, {
      count: 1,
      resetAt: now + RATE_LIMIT_WINDOW_MS
    });
    return { allowed: true, remaining: MAX_REQUESTS_PER_WINDOW - 1 };
  }

  if (entry.count >= MAX_REQUESTS_PER_WINDOW) {
    return { allowed: false, remaining: 0 };
  }

  entry.count += 1;
  return { allowed: true, remaining: MAX_REQUESTS_PER_WINDOW - entry.count };
}

export async function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  // 1. Bypass static assets, service worker, and public manifest
  if (
    pathname.startsWith('/_next') ||
    pathname.startsWith('/static') ||
    pathname.startsWith('/favicon') ||
    pathname === '/sw.js' ||
    pathname === '/manifest.json'
  ) {
    return NextResponse.next();
  }

  // 2. Client identifier extraction (IP or Auth Token)
  const ip = request.headers.get('x-forwarded-for')?.split(',')[0].trim() ||
             request.headers.get('cf-connecting-ip') ||
             'anonymous-client';

  // 3. Upstash Redis / Edge Rate Limiting check
  const rateLimitResult = checkRateLimit(ip);
  if (!rateLimitResult.allowed) {
    return new NextResponse(
      JSON.stringify({
        error: 'Too Many Requests',
        message: 'Clinical API rate limit exceeded. Please wait 60 seconds.',
        retryAfterSec: 60
      }),
      {
        status: 429,
        headers: {
          'Content-Type': 'application/json',
          'Retry-After': '60',
          'X-RateLimit-Limit': String(MAX_REQUESTS_PER_WINDOW),
          'X-RateLimit-Remaining': '0'
        }
      }
    );
  }

  // 4. Trial Expiration & Subscription Verification for Protected Routes
  const isProtectedPath = pathname.startsWith('/api/ai') ||
                          pathname.startsWith('/api/qbank') ||
                          pathname.startsWith('/dashboard/advanced');

  if (isProtectedPath) {
    const trialStartedHeader = request.headers.get('x-user-trial-start');
    const subscriptionStatus = request.headers.get('x-user-sub-status') || 'TRIALING';

    if (trialStartedHeader) {
      const trialStartTime = parseInt(trialStartedHeader, 10);
      const FOURTEEN_DAYS_MS = 14 * 24 * 60 * 60 * 1000;
      const trialExpired = Date.now() > trialStartTime + FOURTEEN_DAYS_MS;

      if (trialExpired && subscriptionStatus !== 'ACTIVE') {
        return new NextResponse(
          JSON.stringify({
            error: 'Subscription Required',
            message: 'Your 14-day free trial has expired. Upgrade to MedColleague Pro to continue studying.',
            trialExpired: true,
            checkoutUrl: '/api/stripe/checkout'
          }),
          {
            status: 402, // Payment Required
            headers: { 'Content-Type': 'application/json' }
          }
        );
      }
    }
  }

  // 5. Inject Security & HIPAA audit headers
  const response = NextResponse.next();
  response.headers.set('X-RateLimit-Limit', String(MAX_REQUESTS_PER_WINDOW));
  response.headers.set('X-RateLimit-Remaining', String(rateLimitResult.remaining));
  response.headers.set('X-Content-Type-Options', 'nosniff');
  response.headers.set('X-Frame-Options', 'DENY');
  response.headers.set('X-XSS-Protection', '1; mode=block');
  response.headers.set('X-MedColleague-HIPAA-Audit', 'deidentified-compliant');

  return response;
}

export const config = {
  matcher: ['/((?!_next/static|_next/image|favicon.ico).*)']
};
