package com.handybook.handybook.core;

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
    private Bus bus;
    protected User user;
    private PrefsManager prefsManager;

    @Inject
    UserManager(final Bus bus, final PrefsManager prefsManager)
    {
        this.prefsManager = prefsManager;
        this.bus = bus;
        this.bus.register(this);
    }

    public User getCurrentUser()
    {
        if (user != null)
        {
            return user;
        }
        else
        {
            if ((user = User.fromJson(prefsManager.getString(PrefsKey.USER))) != null)
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
            prefsManager.setString(PrefsKey.USER, null);
            bus.post(new UserLoggedInEvent(false));
            return;
        }

        user = newUser;
        user.addObserver(this);

        prefsManager.setString(PrefsKey.USER, user.toJson());

        UAirship.shared().getPushManager().setAlias(user.getId());

        bus.post(new UserLoggedInEvent(true));
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
        if (event.getEnvironment() != event.getPrevEnvironment())
        {
            setCurrentUser(null);
        }
    }
}
