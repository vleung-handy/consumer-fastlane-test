package com.handybook.handybook.ratingflow.ui;

import android.os.Bundle;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.BaseActivity;

public class RatingFlowActivity extends BaseActivity {

    private Booking mBooking;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBooking = getIntent().getParcelableExtra(BundleKeys.BOOKING);
        setContentView(R.layout.activity_rating_flow);
        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .add(
                        R.id.fragment_container,
                        RatingFlowRateAndTipFragment.newInstance(mBooking)
                )
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mUserManager.isUserLoggedIn()) {
            finish();
        }
    }
}
