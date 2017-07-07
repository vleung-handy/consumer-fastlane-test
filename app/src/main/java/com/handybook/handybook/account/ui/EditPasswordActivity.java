package com.handybook.handybook.account.ui;

import android.support.v4.app.Fragment;

import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;

public final class EditPasswordActivity extends SingleFragmentActivity {

    @Override
    protected final Fragment createFragment() {
        return ProfilePasswordFragment.newInstance();
    }
}
