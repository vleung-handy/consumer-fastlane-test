package com.handybook.handybook.core;

import com.handybook.handybook.promos.splash.SplashPromo;
import com.handybook.handybook.referral.manager.ReferralsManager;
import com.handybook.handybook.referral.model.ReferralResponse;

public interface RequiredModalsLauncher //TODO: rename + move to better package
{

    void showSplashPromo(SplashPromo splashPromo);

    void showBlockingScreen();

    void showReferralDialog(
            ReferralResponse referralResponse,
            ReferralsManager.Source source
    );
}
