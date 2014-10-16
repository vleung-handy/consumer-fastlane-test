package com.handybook.handybook;

public final class UserLoggedInEvent {
    private final boolean isLoggedIn;

    UserLoggedInEvent(final boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public final boolean isLoggedIn() {
        return isLoggedIn;
    }
}
