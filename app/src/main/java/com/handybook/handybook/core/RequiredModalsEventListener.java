package com.handybook.handybook.core;

import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.notifications.splash.SplashNotificationEvent;
import com.handybook.handybook.module.referral.event.ReferralsEvent;
import com.squareup.otto.Subscribe;

public class RequiredModalsEventListener //TODO: rename + move to better package
{
    private RequiredModalsLauncher mRequiredModalsLauncher;

    public RequiredModalsEventListener(RequiredModalsLauncher requiredModalsLauncher)
    {
        mRequiredModalsLauncher = requiredModalsLauncher;
    }

    @Subscribe
    public void onReceiveSplashPromoSuccess(SplashNotificationEvent.ReceiveAvailableSplashPromoSuccess event)
    {
        mRequiredModalsLauncher.showSplashPromo(event.splashPromo);
    }

    @Subscribe
    public void onStartBlockingApp(HandyEvent.StartBlockingAppEvent event)
    {
        mRequiredModalsLauncher.showBlockingScreen();
    }

    @Subscribe
    public void onReceivePrepareReferralsSuccess(final ReferralsEvent.ReceivePrepareReferralsSuccess event)
    {
        if (event.isForDialog())
        {
            mRequiredModalsLauncher.showReferralDialog(
                    event.getReferralResponse(),
                    event.getSource()
            );
        }
    }

}
