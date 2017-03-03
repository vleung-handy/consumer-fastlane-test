package com.handybook.handybook.tool.util;

import android.app.Activity;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

public class LauncherActivityTestRule<T extends Activity> extends ActivityTestRule<T> {

    public LauncherActivityTestRule(final Class<T> activityClass) {
        super(activityClass);
    }

    public LauncherActivityTestRule(
            final Class<T> activityClass,
            boolean initialTouchMode,
            boolean launchActivity
    ) {
        super(activityClass, initialTouchMode, launchActivity);
    }

    @Override
    protected Intent getActivityIntent() {
        Intent intent = super.getActivityIntent();
        //app is not closed between tests so make sure each launch is fresh
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return intent;
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();
    }

    @Override
    protected void afterActivityLaunched() {
        super.afterActivityLaunched();
    }

    @Override
    protected void afterActivityFinished() {
        super.afterActivityFinished();
    }
}
