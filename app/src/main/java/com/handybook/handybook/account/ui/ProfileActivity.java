package com.handybook.handybook.account.ui;

import android.support.v4.app.Fragment;

import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;

public final class ProfileActivity extends SingleFragmentActivity {

    @Override
    protected final Fragment createFragment() {
        return AccountFragment.newInstance();
    }
}
