package com.handybook.handybook.notifications.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;
import com.handybook.handybook.notifications.ui.fragment.NotificationFeedFragment;

public final class NotificationsActivity extends SingleFragmentActivity {

    @Override
    protected final Fragment createFragment() {
        return NotificationFeedFragment.newInstance();
    }
}
