package com.handybook.handybook.referral;

import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.manager.DefaultPreferencesManager;
import com.handybook.handybook.referral.manager.ReferralsManager;
import com.handybook.handybook.referral.ui.ProReferralFragment;
import com.handybook.handybook.referral.ui.RedemptionActivity;
import com.handybook.handybook.referral.ui.RedemptionEmailSignUpFragment;
import com.handybook.handybook.referral.ui.RedemptionFragment;
import com.handybook.handybook.referral.ui.RedemptionSignUpFragment;
import com.handybook.handybook.referral.ui.ReferralActivity;
import com.handybook.handybook.referral.ui.ReferralFragment;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                RedemptionActivity.class,
                RedemptionFragment.class,
                RedemptionSignUpFragment.class,
                RedemptionEmailSignUpFragment.class,
                ReferralActivity.class,
                ReferralFragment.class,
                ProReferralFragment.class,
        })
public final class ReferralModule {

    @Provides
    @Singleton
    final ReferralsManager provideReferralsManager(
            final Bus bus,
            final DataManager dataManager,
            final DefaultPreferencesManager defaultPreferencesManager
    ) {
        return new ReferralsManager(bus, dataManager, defaultPreferencesManager);
    }
}
