package com.handybook.handybook.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.fragment.UpdatePaymentFragment;

public class UpdatePaymentActivity extends MenuDrawerActivity
{
    @Override
    protected Fragment createFragment()
    {
        return UpdatePaymentFragment.newInstance();
    }

    @Override
    protected String getNavItemTitle()
    {
        return getString(R.string.payment);
    }
}
