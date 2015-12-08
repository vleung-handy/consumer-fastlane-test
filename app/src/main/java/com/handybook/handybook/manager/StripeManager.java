package com.handybook.handybook.manager;

import com.handybook.handybook.event.StripeEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Token;

import javax.inject.Inject;

public class StripeManager
{
    private Stripe mStripe;
    private Bus mBus;

    @Inject
    public StripeManager(final Stripe stripe, final Bus bus)
    {
        mStripe = stripe;
        mBus = bus;
        mBus.register(this);
    }

    @Subscribe
    public void onRequestCreateToken(StripeEvent.RequestCreateToken event)
    {
        mStripe.createToken(event.card, new TokenCallback()
        {
            @Override
            public void onSuccess(final Token token)
            {
                mBus.post(new StripeEvent.ReceiveCreateTokenSuccess(token));
            }

            @Override
            public void onError(final Exception error)
            {
                mBus.post(new StripeEvent.ReceiveCreateTokenError(error));
            }
        });
    }


}
