package com.handybook.handybook.module.notifications;

import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.manager.PrefsManager;
import com.handybook.handybook.module.notifications.feed.manager.NotificationManager;
import com.handybook.handybook.module.notifications.feed.ui.activity.NotificationsActivity;
import com.handybook.handybook.module.notifications.feed.ui.fragment.NotificationFeedFragment;
import com.handybook.handybook.module.notifications.splash.manager.SplashNotificationManager;
import com.handybook.handybook.module.notifications.splash.view.fragment.SplashPromoDialogFragment;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                NotificationsActivity.class,
                NotificationFeedFragment.class,
                SplashPromoDialogFragment.class,
        })
public final class NotificationsModule
{
    @Provides
    @Singleton
    final SplashNotificationManager provideSplashNotificationManager(
            final UserManager userManager,
            final DataManager dataManager,
            final PrefsManager prefsManager,
            final Bus bus
    )
    {
        return new SplashNotificationManager(userManager, dataManager, prefsManager, bus);
    }

    @Provides
    @Singleton
    final NotificationManager provideNotificationManager(
            final Bus bus,
            final DataManager dataManager
    )
    {
        return new NotificationManager(bus, dataManager);
    }
}
