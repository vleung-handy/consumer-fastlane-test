package com.handybook.handybook.manager;

import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.constant.ConfigKeys;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.event.StripeEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Token;

import java.util.Properties;

import javax.inject.Inject;

public class StripeManager
{
    private Bus mBus;
    private Properties mConfig;

    @Inject
    public StripeManager(final Bus bus, final Properties config)
    {
        mBus = bus;
        mConfig = config;
        mBus.register(this);
    }

    @Subscribe
    public void onRequestCreateToken(StripeEvent.RequestCreateToken event)
    {
        new Stripe().createToken(event.card, getStripeKey(), new TokenCallback()
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

    private String getStripeKey()
    {
        String stripeKey = mConfig.getProperty(ConfigKeys.STRIPE_PUBLISHABLE_KEY);
        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_STAGE))
        {
            stripeKey = mConfig.getProperty(ConfigKeys.STRIPE_PUBLISHABLE_KEY_INTERNAL);
        }
        return stripeKey;
    }

}
