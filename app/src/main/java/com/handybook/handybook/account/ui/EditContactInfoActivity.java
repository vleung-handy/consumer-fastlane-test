package com.handybook.handybook.account.ui;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

public final class EditContactInfoActivity extends MenuDrawerActivity {

    @Override
    protected boolean requiresUser() {
        return true;
    }

    @Override
    protected final Fragment createFragment() {
        return ContactFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.account_contact_info);
    }
}
