package com.handybook.handybook.manager;

import android.app.Application;
import android.content.Context;

import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.R;
import com.handybook.handybook.constant.NotificationActions;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.event.UserLoggedInEvent;
import com.handybook.handybook.util.NotificationActionUtils;
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
    public UrbanAirshipManager(Context context, Bus bus, UserManager userManager)
    {
        mUserManager = userManager;
        if (!UAirship.isTakingOff() && !UAirship.isFlying())
        {
            startUrbanAirship((Application) context.getApplicationContext());
        }
        bus.register(this);
    }

    @Subscribe
    public void onUserLoggedIn(UserLoggedInEvent event)
    {
        if (event.isLoggedIn())
        {
            setUniqueIdentifiers(UAirship.shared());
        }
    }

    private void startUrbanAirship(final Application application)
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
                setNotificationActionButtons(airship);
                setUniqueIdentifiers(airship);
            }
        });
    }

    private void setUniqueIdentifiers(final UAirship airship)
    {
        final User currentUser = mUserManager.getCurrentUser();
        if (UAirship.isFlying() && currentUser != null)
        {
            final String userId = currentUser.getId();

            // Keep alias around for backwards compatibility until named user is backfilled by UrbanAirship
            airship.getPushManager().setAlias(userId);
            airship.getPushManager().getNamedUser().setId(userId);
        }
    }

    private void setNotificationActionButtons(final UAirship airship)
    {
        airship.getPushManager()
                .addNotificationActionButtonGroup(NotificationActions.ACTION_GROUP_CONTACT,
                        NotificationActionUtils.getContactActionButtonGroup());
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
}
