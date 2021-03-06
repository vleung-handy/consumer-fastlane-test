package com.handybook.handybook.core.manager;

import com.handybook.handybook.core.event.StripeEvent;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Token;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Properties;

import javax.inject.Inject;

public class StripeManager {

    private EventBus mBus;
    private Properties mConfig;

    @Inject
    public StripeManager(final EventBus bus, final Properties config) {
        mBus = bus;
        mConfig = config;
        mBus.register(this);
    }

    @Subscribe
    public void onRequestCreateToken(StripeEvent.RequestCreateToken event) {
        new Stripe().createToken(event.getCard(), event.getStripeKey(), new TokenCallback() {
            @Override
            public void onSuccess(final Token token) {
                mBus.post(new StripeEvent.ReceiveCreateTokenSuccess(token));
            }

            @Override
            public void onError(final Exception error) {
                mBus.post(new StripeEvent.ReceiveCreateTokenError(error));
            }
        });
    }

}
