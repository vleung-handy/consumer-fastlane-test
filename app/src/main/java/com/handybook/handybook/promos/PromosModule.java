package com.handybook.handybook.promos;

import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.manager.SecurePreferencesManager;
import com.handybook.handybook.promos.persistent.PersistentPromoCoordinatorLayout;
import com.handybook.handybook.promos.persistent.PersistentPromoManager;
import com.handybook.handybook.promos.splash.SplashPromoDialogFragment;
import com.handybook.handybook.promos.splash.SplashPromoManager;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                PersistentPromoCoordinatorLayout.class,
                SplashPromoDialogFragment.class,
        })
public final class PromosModule {

    @Provides
    @Singleton
    final PersistentPromoManager providePersistentPromoManager(
            final DataManager dataManager
    ) {
        return new PersistentPromoManager(dataManager);
    }

    @Provides
    @Singleton
    final SplashPromoManager provideSplashPromoManager(
            final UserManager userManager,
            final DataManager dataManager,
            final SecurePreferencesManager securePreferencesManager,
            final EventBus bus
    ) {
        return new SplashPromoManager(
                userManager,
                dataManager,
                securePreferencesManager,
                bus
        );
    }
}
