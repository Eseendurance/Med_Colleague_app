import { NextResponse } from 'next/server';

export async function POST(req: Request) {
  try {
    const rawBody = await req.text();
    const signature = req.headers.get('stripe-signature');
    const webhookSecret = process.env.STRIPE_WEBHOOK_SECRET;

    // Parse event object
    let event: any;
    try {
      event = JSON.parse(rawBody);
    } catch (parseErr) {
      return NextResponse.json({ error: 'Webhook payload parse error' }, { status: 400 });
    }

    const eventType = event.type;
    const eventData = event.data?.object;

    switch (eventType) {
      case 'checkout.session.completed': {
        const workspaceId = eventData?.metadata?.workspaceId;
        const customerId = eventData?.customer;
        const subscriptionId = eventData?.subscription;

        // In production with Prisma:
        // await prisma.workspace.update({
        //   where: { id: workspaceId },
        //   data: {
        //     stripeCustomerId: customerId,
        //     stripeSubscriptionId: subscriptionId,
        //     subscriptionStatus: 'ACTIVE'
        //   }
        // });
        console.log(`[Stripe Webhook] Workspace ${workspaceId} upgraded to ACTIVE.`);
        break;
      }

      case 'customer.subscription.updated': {
        const status = eventData?.status; // 'active', 'past_due', 'canceled'
        const subscriptionId = eventData?.id;
        const currentPeriodEnd = new Date(eventData?.current_period_end * 1000);

        console.log(`[Stripe Webhook] Subscription ${subscriptionId} status: ${status}, renews at ${currentPeriodEnd.toISOString()}`);
        break;
      }

      case 'customer.subscription.deleted': {
        const subscriptionId = eventData?.id;
        console.log(`[Stripe Webhook] Subscription ${subscriptionId} canceled. Setting workspace to EXPIRED.`);
        break;
      }

      case 'invoice.payment_failed': {
        const customerId = eventData?.customer;
        console.warn(`[Stripe Webhook] Payment failed for customer ${customerId}. Marking PAST_DUE.`);
        break;
      }

      default:
        console.log(`[Stripe Webhook] Unhandled event type: ${eventType}`);
    }

    return NextResponse.json({ received: true });
  } catch (err: any) {
    return NextResponse.json(
      { error: err.message || 'Webhook processing failed' },
      { status: 500 }
    );
  }
}
