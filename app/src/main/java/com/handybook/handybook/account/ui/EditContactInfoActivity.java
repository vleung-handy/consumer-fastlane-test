package com.handybook.handybook.account.ui;

import android.support.v4.app.Fragment;

import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;

public final class EditContactInfoActivity extends SingleFragmentActivity {

    @Override
    protected final Fragment createFragment() {
        return ContactFragment.newInstance();
    }
}
