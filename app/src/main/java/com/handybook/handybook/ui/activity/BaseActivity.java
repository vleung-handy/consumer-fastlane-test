package com.handybook.handybook.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.R;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.LaundryDropInfo;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.DataManagerErrorHandler;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.ui.fragment.LaundryDropOffDialogFragment;
import com.handybook.handybook.ui.fragment.LaundryInfoDialogFragment;
import com.handybook.handybook.ui.fragment.RateServiceDialogFragment;
import com.handybook.handybook.ui.widget.ProgressDialog;
import com.urbanairship.google.PlayServicesUtils;
import com.yozio.android.Yozio;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends FragmentActivity {
    protected boolean allowCallbacks;
    private OnBackPressedListener onBackPressedListener;
    private RateServiceDialogFragment rateServiceDialog;
    protected ProgressDialog progressDialog;
    protected Toast toast;

    @Inject Mixpanel mixpanel;
    @Inject UserManager userManager;
    @Inject DataManager dataManager;
    @Inject DataManagerErrorHandler dataManagerErrorHandler;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Crashlytics.start(this);
        Yozio.initialize(this);

        ((BaseApplication)this.getApplication()).inject(this);

        if (!BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_STAGE)
                && !BuildConfig.BUILD_TYPE.equals("debug")) {
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Yozio.YOZIO_ENABLE_LOGGING = false;
        }

        final Intent intent = getIntent();
        final Uri data = intent.getData();

        if (data != null && data.getHost() != null && data.getHost().equals("deeplink.yoz.io")) {
            mixpanel.trackEventYozioOpen(Yozio.getMetaData(intent));
        }

        toast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);

        progressDialog = new ProgressDialog(this);
        progressDialog.setDelay(400);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (PlayServicesUtils.isGooglePlayStoreAvailable()) {
            PlayServicesUtils.handleAnyPlayServicesError(this);
        }

        allowCallbacks = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        allowCallbacks = false;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        showRequiredUserModals();
    }

    @Override
    protected final void attachBaseContext(final Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null) onBackPressedListener.onBack();
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mixpanel.flush();
        super.onDestroy();
    }

    public void setOnBackPressedListener(final OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    public interface OnBackPressedListener {
        void onBack();
    }

    private void showRequiredUserModals() {
        final FragmentManager fm = getSupportFragmentManager();
        final User user = userManager.getCurrentUser();

        if (user == null || fm.findFragmentByTag("RateServiceDialogFragment") != null
                || fm.findFragmentByTag("LaundryDropOffDialogFragment") != null
                || fm.findFragmentByTag("LaundryInfoDialogFragment") != null
                || !(BaseActivity.this instanceof ServiceCategoriesActivity)) return;

        dataManager.getUser(user.getId(), user.getAuthToken(), new DataManager.Callback<User>() {
            @Override
            public void onSuccess(final User user) {
                if (!allowCallbacks) return;

                final int laundryBookingId = user.getLaundryBookingId();
                final int addLaundryBookingId = user.getAddLaundryBookingId();
                final String proName = user.getBookingRatePro();
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);

                if (addLaundryBookingId > 0 && !prefs.getBoolean("APP_LAUNDRY_INFO_SHOWN", false)) {
                    showLaundryInfoModal(addLaundryBookingId, user.getAuthToken());
                }
                else if (laundryBookingId > 0) showLaundryDropOffModal(laundryBookingId, user.getAuthToken());
                else if (proName != null) {
                    final int bookingId = user.getBookingRateId();

                    rateServiceDialog = RateServiceDialogFragment
                            .newInstance(bookingId, proName, -1);

                    rateServiceDialog.show(BaseActivity.this.getSupportFragmentManager(),
                            "RateServiceDialogFragment");

                    mixpanel.trackEventProRate(Mixpanel.ProRateEventType.SHOW, bookingId,
                            proName, 0);
                }
            }

            @Override
            public void onError(DataManager.DataManagerError error) {}
        });
    }

    private void showLaundryDropOffModal(final int bookingId, final String authToken) {
        dataManager.getLaundryScheduleInfo(bookingId, authToken,
                new DataManager.Callback<LaundryDropInfo>() {
            @Override
            public void onSuccess(final LaundryDropInfo info) {
                if (!allowCallbacks) return;

                LaundryDropOffDialogFragment.newInstance(bookingId, info)
                        .show(BaseActivity.this.getSupportFragmentManager(), "LaundryDropOffDialogFragment");
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {}
        });
    }

    private void showLaundryInfoModal(final int bookingId, final String authToken) {
        dataManager.getAddLaundryInfo(bookingId, authToken, new DataManager.Callback<Booking>() {
            @Override
            public void onSuccess(final Booking booking) {
                if (!allowCallbacks) return;

                LaundryInfoDialogFragment.newInstance(booking)
                        .show(BaseActivity.this.getSupportFragmentManager(), "LaundryInfoDialogFragment");
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {}
        });
    }
}
