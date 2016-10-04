package com.handybook.handybook.core;

import android.content.Context;

import com.handybook.handybook.manager.SecurePreferencesManager;
import com.squareup.otto.Bus;

public class TestUserManager extends UserManager
{
    private User mUser;

    TestUserManager(
            final Context context,
            final Bus bus,
            final SecurePreferencesManager securePreferencesManager
    )
    {
        super(context, bus, securePreferencesManager);
        mUser = new User();
        mUser.setFirstName("Test");
        mUser.setLastName("User");
        mUser.setEmail("test@test.com");
        mUser.setPhone("123456789");
        mUser.setPhonePrefix("0");
    }

    @Override
    public User getCurrentUser() { return mUser; }
}
