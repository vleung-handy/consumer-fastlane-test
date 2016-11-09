package com.handybook.handybook.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;

import butterknife.ButterKnife;

public class OldDeeplinkSplashActivity extends BaseActivity {
    private static final String STATE_LAUNCHED_NEXT = "LAUNCHED_NEXT";

    private User user;
    private boolean launchedNext;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        if (savedInstanceState != null)
        {
            launchedNext = savedInstanceState.getBoolean(STATE_LAUNCHED_NEXT, false);
        }

        if (!launchedNext)
        {
            user = mUserManager.getCurrentUser();

            final Intent intent = this.getIntent();
            final String action = intent.getAction();
            final Uri data = intent.getData();

            if (!action.equals("android.intent.action.VIEW") || !data.getScheme().equals("handy")) {
                openServiceCategoriesActivity();
                return;
            }

            mNavigationManager.handleSplashScreenLaunch(this.getIntent(), this);
            launchedNext = true;
        }
        else
        {
            openServiceCategoriesActivity();
        }
    }

    @Override
    public void startActivity(final Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        super.startActivity(intent);

        launchedNext = true;
        finish();
    }

    @Override
    public void startActivityForResult(final Intent intent, final int resultCode) {
        super.startActivityForResult(intent, resultCode);
        launchedNext = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActivityResult.RESCHEDULE_NEW_DATE) {
            openServiceCategoriesActivity();
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_LAUNCHED_NEXT, launchedNext);
    }

    private void openServiceCategoriesActivity() {
        startActivity(new Intent(this, ServiceCategoriesActivity.class));
    }

    private void openRescheduleActivity(final String bookingId) {
        mDataManager.getBooking(bookingId,
                new DataManager.Callback<Booking>() {
                    @Override
                    public void onSuccess(final Booking booking) {
                        if (!allowCallbacks) return;

                        mDataManager.getPreRescheduleInfo(bookingId, new DataManager.Callback<String>()
                        {
                            @Override
                            public void onSuccess(final String notice) {
                                if (!allowCallbacks) return;

                                final Intent intent = new Intent(OldDeeplinkSplashActivity.this, BookingDateActivity.class);
                                intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, booking);
                                intent.putExtra(BundleKeys.RESCHEDULE_NOTICE, notice);
                                startActivityForResult(intent, ActivityResult.RESCHEDULE_NEW_DATE);
                            }

                            @Override
                            public void onError(final DataManager.DataManagerError error) {
                                if (!allowCallbacks) return;
                                mDataManagerErrorHandler.handleError(OldDeeplinkSplashActivity.this, error);
                                openServiceCategoriesActivity();
                            }
                        });
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
                        if (!allowCallbacks) return;
                        mDataManagerErrorHandler.handleError(OldDeeplinkSplashActivity.this, error);
                        openServiceCategoriesActivity();
                    }
                });
    }
}
