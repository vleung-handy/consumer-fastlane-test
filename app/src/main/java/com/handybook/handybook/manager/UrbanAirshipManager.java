package com.handybook.handybook.manager;

import android.app.Application;

import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.R;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.event.UserLoggedInEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.push.notifications.DefaultNotificationFactory;
import com.urbanairship.push.notifications.NotificationFactory;

import javax.inject.Inject;

public class UrbanAirshipManager
{
    private UserManager mUserManager;

    @Inject
    public UrbanAirshipManager(Bus bus, UserManager userManager)
    {
        mUserManager = userManager;
        bus.register(this);
    }

    @Subscribe
    public void onUserLoggedIn(UserLoggedInEvent event)
    {
        if (event.isLoggedIn())
        {
            setUniqueIdentifiers();
        }
    }

    @Subscribe
    public void onApplicationCreated(HandyEvent.ApplicationCreated event)
    {
        if (!UAirship.isTakingOff() && !UAirship.isFlying())
        {
            startUrbanAirship(event.getApplication());
        }
    }

    protected void startUrbanAirship(final Application application)
    {
        final AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(application);
        options.inProduction = BuildConfig.FLAVOR.equalsIgnoreCase(BaseApplication.FLAVOR_PROD);

        UAirship.takeOff(application, options, new UAirship.OnReadyCallback()
        {
            @Override
            public void onAirshipReady(final UAirship airship)
            {
                final NotificationFactory notificationFactory = getNotificationFactory(application);
                airship.getPushManager().setNotificationFactory(notificationFactory);
                airship.getPushManager().setPushEnabled(true);
                // Notifications the user can see as opposed to silent pushes
                airship.getPushManager().setUserNotificationsEnabled(true);

                setUniqueIdentifiers();
            }
        });
    }

    private NotificationFactory getNotificationFactory(final Application application)
    {
        final DefaultNotificationFactory defaultNotificationFactory =
                new DefaultNotificationFactory(application);

        defaultNotificationFactory.setColor(application.getResources()
                .getColor(R.color.handy_blue));
        defaultNotificationFactory.setSmallIconId(R.drawable.ic_notification);
        return defaultNotificationFactory;
    }

    private void setUniqueIdentifiers()
    {
        final User currentUser = mUserManager.getCurrentUser();
        if (UAirship.isFlying() && currentUser != null)
        {
            final String userId = currentUser.getId();

            // Keep alias around for backwards compatibility until named user is backfilled by UrbanAirship
            UAirship.shared().getPushManager().setAlias(userId);
            UAirship.shared().getPushManager().getNamedUser().setId(userId);
        }
    }
}
