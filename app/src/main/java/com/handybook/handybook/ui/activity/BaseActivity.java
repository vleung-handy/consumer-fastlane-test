package com.handybook.handybook.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.ui.fragment.RateServiceDialogFragment;
import com.urbanairship.google.PlayServicesUtils;
import com.yozio.android.Yozio;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends FragmentActivity {
    private boolean allowCallbacks;
    private OnBackPressedListener onBackPressedListener;
    private RateServiceDialogFragment rateServiceDialog;

    @Inject Mixpanel mixpanel;
    @Inject UserManager userManager;
    @Inject DataManager dataManager;

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

        // check if user has a booking to rate
        checkRequiredRatings();
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

    private void checkRequiredRatings() {
        final User user = userManager.getCurrentUser();

        if (user != null) {
            final FragmentManager fm = getSupportFragmentManager();

            rateServiceDialog
                    = (RateServiceDialogFragment)fm.findFragmentByTag("RateServiceDialogFragment");

            if (rateServiceDialog == null) {
                dataManager.getUser(user.getId(), user.getAuthToken(), new DataManager.Callback<User>() {
                    @Override
                    public void onSuccess(final User user) {
                        if (!allowCallbacks) return;

                        if (user.getBookingRatePro() != null
                                && BaseActivity.this instanceof ServiceCategoriesActivity) {
                            rateServiceDialog = RateServiceDialogFragment
                                    .newInstance(user.getBookingRateId(), -1);

                            rateServiceDialog.show(BaseActivity.this.getSupportFragmentManager(),
                                    "RateServiceDialogFragment");
                        }
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error) {}
                });
            }
        }
    }
}
