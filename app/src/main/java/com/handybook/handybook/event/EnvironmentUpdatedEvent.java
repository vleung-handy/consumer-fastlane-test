package com.handybook.handybook.event;

public final class EnvironmentUpdatedEvent {
    private final String mPreviousEnvironment;
    private final String mCurrentEnvironment;

    public EnvironmentUpdatedEvent(final String currentEnvironment, final String previousEnvironment)
    {
        mCurrentEnvironment = currentEnvironment;
        mPreviousEnvironment = previousEnvironment;
    }

    public final String getEnvironment()
    {
        return mCurrentEnvironment;
    }

    public final String getPrevEnvironment()
    {
        return mPreviousEnvironment;
    }
}
