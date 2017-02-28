package com.handybook.handybook.core.event;

public final class UserLoggedInEvent {

    private final boolean isLoggedIn;

    public UserLoggedInEvent(final boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public final boolean isLoggedIn() {
        return isLoggedIn;
    }
}
