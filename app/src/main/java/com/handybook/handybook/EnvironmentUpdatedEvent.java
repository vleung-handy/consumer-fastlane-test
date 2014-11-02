package com.handybook.handybook;

public final class EnvironmentUpdatedEvent {
    private final DataManager.Environment prev;
    private final DataManager.Environment current;

    EnvironmentUpdatedEvent(final DataManager.Environment current,
                            final DataManager.Environment prev) {
        this.current = current;
        this.prev = prev;
    }

    public final DataManager.Environment getEnvironment() {
        return current;
    }
    public final DataManager.Environment getPrevEnvironment() {
        return prev;
    }
}
