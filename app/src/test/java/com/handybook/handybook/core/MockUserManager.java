package com.handybook.handybook.core;

import com.handybook.handybook.data.SecurePreferences;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Created by jwilliams on 3/4/15.
 */
public class MockUserManager extends UserManager {

    @Inject
    public MockUserManager(final Bus bus, final SecurePreferences prefs) {
        super(bus, prefs);
    }

    public User getCurrentUser() {
        return user;
    }

    public void setCurrentUser(final User newUser) {
        user = newUser;
    }
}
