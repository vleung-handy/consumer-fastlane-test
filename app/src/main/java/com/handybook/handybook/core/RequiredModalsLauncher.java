package com.handybook.handybook.core;

import com.handybook.handybook.module.notifications.splash.model.SplashPromo;
import com.handybook.handybook.module.referral.model.ReferralResponse;

public interface RequiredModalsLauncher //TODO: rename + move to better package
{
    void showSplashPromo(SplashPromo splashPromo);
    void showBlockingScreen();
    void showReferralDialog(ReferralResponse referralResponse, String eventContext);
}
