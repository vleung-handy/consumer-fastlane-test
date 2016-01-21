package com.handybook.handybook.core;

import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.notifications.splash.SplashNotificationEvent;
import com.squareup.otto.Subscribe;

public class RequiredModalsEventListener //TODO: rename + move to better package
{
    private RequiredModalsLauncher mRequiredModalsLauncher;
    public RequiredModalsEventListener(RequiredModalsLauncher requiredModalsLauncher)
    {
        mRequiredModalsLauncher = requiredModalsLauncher;
    }

    @Subscribe
    public void onRequestShowSplashPromo(SplashNotificationEvent.RequestShowSplashPromo event)
    {
        mRequiredModalsLauncher.showSplashPromo(event.splashPromo);
    }

    @Subscribe
    public void onStartBlockingApp(HandyEvent.StartBlockingAppEvent event)
    {
        mRequiredModalsLauncher.showBlockingScreen();
    }

}
