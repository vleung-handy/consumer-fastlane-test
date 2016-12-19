package com.handybook.handybook.notifications.feed.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.notifications.feed.ui.fragment.NotificationFeedFragment;


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
