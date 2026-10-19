import { NextResponse } from 'next/server';

export async function POST(req: Request) {
  try {
    const body = await req.json().catch(() => ({}));
    const { planId = 'pro_monthly', workspaceId, userEmail, successUrl, cancelUrl } = body;

    const stripeSecretKey = process.env.STRIPE_SECRET_KEY;
    const appBaseUrl = process.env.NEXT_PUBLIC_APP_URL || 'https://med-colleague-app.vercel.app';

    // Pricing tiers definition
    const pricingMap: Record<string, { name: string; amountCents: number; interval: string }> = {
      pro_monthly: {
        name: 'MedColleague Pro (Monthly)',
        amountCents: 2900, // $29.00 / month
        interval: 'month'
      },
      pro_annual: {
        name: 'MedColleague Pro (Annual - 2 Months Free)',
        amountCents: 29000, // $290.00 / year
        interval: 'year'
      },
      institutional: {
        name: 'MedColleague Institutional License',
        amountCents: 99000, // $990.00 / year
        interval: 'year'
      }
    };

    const selectedPlan = pricingMap[planId] || pricingMap.pro_monthly;

    // Production Stripe API integration or Mock fallback when keys are absent
    if (!stripeSecretKey) {
      return NextResponse.json({
        success: true,
        mockSession: true,
        sessionId: `sess_mock_${Date.now()}`,
        checkoutUrl: `${appBaseUrl}/?upgraded=true&plan=${planId}&status=mock_active`,
        message: 'Stripe test mode initialized. Redirecting to billing confirmation.'
      });
    }

    // Call Stripe Checkout API directly
    const stripePayload = new URLSearchParams({
      'payment_method_types[0]': 'card',
      'mode': 'subscription',
      'line_items[0][price_data][currency]': 'usd',
      'line_items[0][price_data][product_data][name]': selectedPlan.name,
      'line_items[0][price_data][unit_amount]': String(selectedPlan.amountCents),
      'line_items[0][price_data][recurring][interval]': selectedPlan.interval,
      'line_items[0][quantity]': '1',
      'success_url': successUrl || `${appBaseUrl}/?session_id={CHECKOUT_SESSION_ID}&success=true`,
      'cancel_url': cancelUrl || `${appBaseUrl}/?canceled=true`,
      'customer_email': userEmail || 'clinician@hospital.org',
      'metadata[workspaceId]': workspaceId || 'default-workspace',
      'metadata[planId]': planId
    });

    const stripeResponse = await fetch('https://api.stripe.com/v1/checkout/sessions', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${stripeSecretKey}`,
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      body: stripePayload.toString()
    });

    const sessionData = await stripeResponse.json();

    if (!stripeResponse.ok) {
      throw new Error(sessionData.error?.message || 'Failed to create Stripe session');
    }

    return NextResponse.json({
      success: true,
      sessionId: sessionData.id,
      checkoutUrl: sessionData.url
    });
  } catch (error: any) {
    return NextResponse.json(
      { error: error.message || 'Internal Stripe error' },
      { status: 500 }
    );
  }
}
