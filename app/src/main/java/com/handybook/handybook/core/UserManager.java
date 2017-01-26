package com.handybook.handybook.core;

import android.content.Context;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.event.EnvironmentUpdatedEvent;
import com.handybook.handybook.core.event.UserLoggedInEvent;
import com.handybook.handybook.core.manager.SecurePreferencesManager;
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
    private User mUser;
    private SecurePreferencesManager mSecurePreferencesManager;

    @Inject
    UserManager(
            final Context context,
            final Bus bus,
            final SecurePreferencesManager securePreferencesManager
    )
    {
        mContext = context;
        mSecurePreferencesManager = securePreferencesManager;
        mBus = bus;
        mBus.register(this);
    }

    /**
     * TODO this seems like a dangerous way of checking if the user is logged in but this logic is
     * currently used everywhere throughout the app
     *
     * @return
     */
    public boolean isUserLoggedIn()
    {
        return getCurrentUser() != null;
    }

    @Nullable
    public User getCurrentUser()
    {
        if (mUser != null)
        {
            return mUser;
        }
        else
        {
            if ((mUser = User.fromJson(mSecurePreferencesManager.getString(PrefsKey.USER))) != null)
            {
                mUser.addObserver(this);
            }
            return mUser;
        }
    }

    public void setCurrentUser(final User newUser)
    {
        if (mUser != null)
        {
            mUser.deleteObserver(this);
        }

        if (newUser == null || newUser.getAuthToken() == null || newUser.getId() == null)
        {
            mUser = null;
            mSecurePreferencesManager.removeValue(PrefsKey.USER);
            Button.getButton(mContext).logout();
            Crashlytics.setUserEmail(null);
            mBus.post(new UserLoggedInEvent(false));
            return;
        }

        mUser = newUser;
        mUser.addObserver(this);

        mSecurePreferencesManager.setString(PrefsKey.USER, mUser.toJson());

        UAirship.shared().getPushManager().setAlias(mUser.getId());
        Button.getButton(mContext).setUserIdentifier(mUser.getId());
        Crashlytics.setUserEmail(mUser.getEmail());
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
