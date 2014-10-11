package com.handybook.handybook;

import android.content.Context;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.inject.Inject;

public final class UserManager implements Observer {
    private final Context context;
    private User user;
    private final SecurePreferences securePrefs;

    @Inject
    UserManager(final Context context) {
        this.context = context;

        final Properties configs = PropertiesReader.getProperties(context, "config.properties");
        securePrefs = new SecurePreferences(context, null,
                configs.getProperty("secure_prefs_key"), true);
    }

    User getCurrentUser() {
        if (user != null) return user;
        else {
            Gson gson = new Gson();
            return gson.fromJson(securePrefs.getString("USER_OBJ"), User.class);
        }
    }

    final void setCurrentUser(final User newUser) {
        if (newUser == null || newUser.getAuthToken() == null || newUser.getId() == null) {
            if (user != null) user.deleteObserver(this);
            user = null;
            securePrefs.put("USER_OBJ", null);
            return;
        }

        if (user != null) user.deleteObserver(this);
        user = newUser;
        user.addObserver(this);

        final Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(final FieldAttributes f) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(final Class<?> clazz) {
                return clazz.equals(Observer.class);
            }
        }).create();
        securePrefs.put("USER_OBJ", gson.toJson(user));
    }

    @Override
    public void update(final Observable observable, final Object data) {
        if (observable instanceof User) setCurrentUser((User)observable);
    }
}
