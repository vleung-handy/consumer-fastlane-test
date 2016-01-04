package com.handybook.handybook.core;

import com.handybook.handybook.module.notifications.splash.model.SplashPromo;

public interface RequiredModalsLauncher //TODO: rename + move to better package
{
    void showSplashPromo(SplashPromo splashPromo);
    void showBlockingScreen();
}
