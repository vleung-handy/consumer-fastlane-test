package com.handybook.handybook.core;

import dagger.ObjectGraph;

/**
 * Created by jwilliams on 3/3/15.
 */
public class TestBaseApplication extends BaseApplication {

    public boolean mIsNewlyLaunched;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void createObjectGraph() {
        graph = ObjectGraph.create(new TestApplicationModule(this.getApplicationContext()));
        graph.inject(this);
    }

    @Override
    public void updateUser() {
    }

    @Override
    public boolean isNewlyLaunched()
    {
        return mIsNewlyLaunched;
    }

    public void setNewlyLaunched(final boolean newlyLaunched)
    {
        mIsNewlyLaunched = newlyLaunched;
    }
}
