package com.handybook.handybook;

import android.content.Context;

import com.google.gson.Gson;
import com.squareup.otto.Bus;

import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

public final class UserManager implements Observer {
    private final Context context;
    private final Bus bus;
    private User user;
    private final SecurePreferences securePrefs;

    @Inject
    UserManager(final Context context, final Bus bus, final SecurePreferences prefs) {
        this.context = context;
        this.bus = bus;
        this.securePrefs = prefs;
    }

    User getCurrentUser() {
        if (user != null) return user;
        else return new Gson().fromJson(securePrefs.getString("USER_OBJ"), User.class);
    }

    final void setCurrentUser(final User newUser) {
        if (newUser == null || newUser.getAuthToken() == null || newUser.getId() == null) {
            if (user != null) user.deleteObserver(this);
            user = null;
            securePrefs.put("USER_OBJ", null);
            bus.post(new UserLoggedInEvent(false));
            return;
        }

        if (user != null) user.deleteObserver(this);
        user = newUser;
        user.addObserver(this);

        securePrefs.put("USER_OBJ", user.toJson());
        bus.post(new UserLoggedInEvent(true));

        //TODO use gson like with services for secure prefs conversion
    }

    @Override
    public void update(final Observable observable, final Object data) {
        if (observable instanceof User) setCurrentUser((User)observable);
    }
}

