package com.handybook.handybook.core;

import dagger.ObjectGraph;

public class TestBaseApplication extends BaseApplication {

    public boolean mIsNewlyLaunched;

    @Override
    protected ObjectGraph createObjectGraph() {
        return ObjectGraph.create(new TestApplicationModule(this.getApplicationContext()));
    }

    @Override
    public void updateUser() {
    }

    @Override
    public boolean isNewlyLaunched() {
        return mIsNewlyLaunched;
    }

    public void setNewlyLaunched(final boolean newlyLaunched) {
        mIsNewlyLaunched = newlyLaunched;
    }
}
