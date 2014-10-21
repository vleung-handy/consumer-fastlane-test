package com.handybook.handybook;

public final class EnvironmentUpdatedEvent {
    private final DataManager.Environment env;

    EnvironmentUpdatedEvent(final DataManager.Environment env) {
        this.env = env;
    }

    public final DataManager.Environment getEnvironment() {
        return env;
    }
}
