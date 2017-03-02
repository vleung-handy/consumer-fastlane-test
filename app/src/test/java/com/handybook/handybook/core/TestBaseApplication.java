package com.handybook.handybook.core;

import dagger.ObjectGraph;

public class TestBaseApplication extends BaseApplication {

    public boolean mIsNewlyLaunched;

    @Override
    protected void createObjectGraph() {
        graph = ObjectGraph.create(new TestApplicationModule(this.getApplicationContext()));
        graph.inject(this);
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
