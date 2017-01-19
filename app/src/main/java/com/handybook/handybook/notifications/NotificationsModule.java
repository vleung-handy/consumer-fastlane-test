package com.handybook.handybook.notifications;

import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.manager.SecurePreferencesManager;
import com.handybook.handybook.notifications.feed.manager.NotificationManager;
import com.handybook.handybook.notifications.feed.ui.activity.NotificationsActivity;
import com.handybook.handybook.notifications.feed.ui.fragment.NotificationFeedFragment;
import com.handybook.handybook.notifications.splash.manager.SplashNotificationManager;
import com.handybook.handybook.notifications.splash.view.fragment.SplashPromoDialogFragment;
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
            final SecurePreferencesManager securePreferencesManager,
            final Bus bus
    )
    {
        return new SplashNotificationManager(
                userManager,
                dataManager,
                securePreferencesManager,
                bus
        );
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