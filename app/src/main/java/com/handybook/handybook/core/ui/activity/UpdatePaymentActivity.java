package com.handybook.handybook.core.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.account.ui.UpdatePaymentFragment;

public class UpdatePaymentActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return UpdatePaymentFragment.newInstance();
    }
}
