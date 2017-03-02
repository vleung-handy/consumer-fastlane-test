package com.handybook.handybook.core.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.account.ui.UpdatePaymentFragment;

public class UpdatePaymentActivity extends MenuDrawerActivity {

    @Override
    protected boolean requiresUser() {
        return true;
    }

    @Override
    protected Fragment createFragment() {
        return UpdatePaymentFragment.newInstance();
    }

    @Override
    protected String getNavItemTitle() {
        return getString(R.string.payment);
    }
}
