package com.handybook.handybook.booking.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.fragment.ServicesFragment;
import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;

public final class ServicesActivity extends SingleFragmentActivity {

    public static final String EXTRA_SERVICE = "com.handy.handy.EXTRA_SERVICE";

    ServicesFragment mFragment;

    @Override
    protected final Fragment createFragment() {
        final Service service = getIntent().getParcelableExtra(EXTRA_SERVICE);
        mFragment = ServicesFragment.newInstance(service);
        return mFragment;
    }
}
