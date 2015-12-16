package com.handybook.handybook.module.notifications.view.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.module.notifications.view.fragment.NotificationFeedFragment;


public final class NotificationsActivity extends MenuDrawerActivity
{
    @Override
    protected final Fragment createFragment()
    {
        return NotificationFeedFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle()
    {
        return getString(R.string.home);
    }
}
