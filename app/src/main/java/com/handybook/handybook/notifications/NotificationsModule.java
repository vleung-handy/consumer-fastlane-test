package com.handybook.handybook.notifications;

import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.notifications.manager.NotificationManager;
import com.handybook.handybook.notifications.ui.activity.NotificationsActivity;
import com.handybook.handybook.notifications.ui.fragment.NotificationFeedFragment;
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
        })
public final class NotificationsModule {

    @Provides
    @Singleton
    final NotificationManager provideNotificationManager(
            final Bus bus,
            final DataManager dataManager
    ) {
        return new NotificationManager(bus, dataManager);
    }
}
