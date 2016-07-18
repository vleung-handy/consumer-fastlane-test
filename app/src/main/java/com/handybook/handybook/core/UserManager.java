package com.handybook.handybook.core;

import android.content.Context;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.event.EnvironmentUpdatedEvent;
import com.handybook.handybook.event.UserLoggedInEvent;
import com.handybook.handybook.manager.PrefsManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.urbanairship.UAirship;
import com.usebutton.sdk.Button;

import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

public class UserManager implements Observer
{
    private Context mContext;
    private Bus mBus;
    protected User user;
    private PrefsManager mPrefsManager;

    @Inject
    UserManager(final Context context, final Bus bus, final PrefsManager prefsManager)
    {
        mContext = context;
        mPrefsManager = prefsManager;
        mBus = bus;
        mBus.register(this);
    }

    public boolean isUserLoggedIn()
    {
        return getCurrentUser() != null;
    }

    @Nullable
    public User getCurrentUser()
    {
        if (user != null)
        {
            return user;
        }
        else
        {
            if ((user = User.fromJson(mPrefsManager.getString(PrefsKey.USER))) != null)
            {
                user.addObserver(this);
            }
            return user;
        }
    }

    public void setCurrentUser(final User newUser)
    {
        if (user != null)
        {
            user.deleteObserver(this);
        }

        if (newUser == null || newUser.getAuthToken() == null || newUser.getId() == null)
        {
            user = null;
            mPrefsManager.removeValue(PrefsKey.USER);
            Button.getButton(mContext).logout();
            Crashlytics.setUserEmail(null);
            mBus.post(new UserLoggedInEvent(false));
            return;
        }

        user = newUser;
        user.addObserver(this);

        mPrefsManager.setString(PrefsKey.USER, user.toJson());

        UAirship.shared().getPushManager().setAlias(user.getId());
        Button.getButton(mContext).setUserIdentifier(user.getId());
        Crashlytics.setUserEmail(user.getEmail());
        mBus.post(new UserLoggedInEvent(true));
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
        if (event.getEnvironment() != null && !event.getEnvironment().equals(event.getPrevEnvironment()))
        {
            setCurrentUser(null);
        }
    }
}
