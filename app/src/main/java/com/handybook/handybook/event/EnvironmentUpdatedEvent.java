package com.handybook.handybook.event;

public final class EnvironmentUpdatedEvent {
    private final String prev;
    private final String current;

    public EnvironmentUpdatedEvent(final String current,
                            final String prev) {
        this.current = current;
        this.prev = prev;
    }

    public final String getEnvironment() {
        return current;
    }
    public final String getPrevEnvironment() {
        return prev;
    }
}
