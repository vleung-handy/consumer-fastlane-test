package com.handybook.handybook.core;

import android.content.Context;

import com.handybook.handybook.core.manager.DefaultPreferencesManager;
import com.handybook.handybook.core.manager.SecurePreferencesManager;
import com.squareup.otto.Bus;

public class TestUserManager extends UserManager
{
    private User mUser;

    TestUserManager(
            final Context context,
            final Bus bus,
            final SecurePreferencesManager securePreferencesManager,
            final DefaultPreferencesManager defaultPreferencesManager
            )
    {
        super(context, bus, securePreferencesManager, defaultPreferencesManager);
        mUser = new User();
        mUser.setFirstName("Test");
        mUser.setLastName("User");
        mUser.setEmail("test@test.com");
        mUser.setPhone("123456789");
        mUser.setPhonePrefix("0");

        User.Analytics analytics = new User.Analytics();
        analytics.setTotalBookings(11);
        mUser.setAnalytics(analytics);
    }

    @Override
    public User getCurrentUser() { return mUser; }
}
