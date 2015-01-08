package com.handybook.handybook.event;

import com.handybook.handybook.data.DataManager;

public final class EnvironmentUpdatedEvent {
    private final DataManager.Environment prev;
    private final DataManager.Environment current;

    public EnvironmentUpdatedEvent(final DataManager.Environment current,
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
