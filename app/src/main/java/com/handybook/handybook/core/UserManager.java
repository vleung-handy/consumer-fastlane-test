package com.handybook.handybook.core;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.event.EnvironmentUpdatedEvent;
import com.handybook.handybook.event.UserLoggedInEvent;
import com.handybook.handybook.manager.PrefsManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.urbanairship.UAirship;

import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

public class UserManager implements Observer
{
    private Bus mBus;
    protected User mUser;
    private PrefsManager mPrefsManager;

    @Inject
    UserManager(final Bus bus, final PrefsManager prefsManager)
    {
        mPrefsManager = prefsManager;
        mBus = bus;
        mBus.register(this);
    }

    public boolean isUserLoggedIn()
    {
        return getCurrentUser() != null;
    }

    public User getCurrentUser()
    {
        if (mUser != null)
        {
            return mUser;
        }
        else
        {
            if ((mUser = User.fromJson(mPrefsManager.getString(PrefsKey.USER))) != null)
            {
                mUser.addObserver(this);
            }
            return mUser; // Can return null here!
        }
    }

    public void setCurrentUser(@NonNull final User newUser)
    {
        if (mUser != null)
        {
            mUser.deleteObserver(this);
        }

        if (newUser == null || newUser.getAuthToken() == null || newUser.getId() == null)
        {
            removeCurrentUser();
            return;
        }

        mUser = newUser;
        mUser.addObserver(this);
        mPrefsManager.setString(PrefsKey.USER, mUser.toJson());
        UAirship.shared().getPushManager().setAlias(mUser.getId());
        Crashlytics.setUserEmail(mUser.getEmail());
        mBus.post(new UserLoggedInEvent(true));
    }

    public void removeCurrentUser(){
        mUser = null;
        mPrefsManager.removeValue(PrefsKey.USER);
        Crashlytics.setUserEmail(null);
        mBus.post(new UserLoggedInEvent(false));
    }

    @Override
    public void update(final Observable observable, final Object data)
    {
        if (observable instanceof User)
        {
            setCurrentUser((User) observable);
        }
    }

    @Subscribe
    public final void environmentUpdated(final EnvironmentUpdatedEvent event)
    {
        final String environment = event.getEnvironment();
        if (environment != null && !environment.equals(event.getPrevEnvironment()))
        {
            removeCurrentUser();
        }
    }

    public boolean isLoggedIn()
    {
        return getCurrentUser() != null;
    }
}
