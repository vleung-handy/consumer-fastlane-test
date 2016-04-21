package com.handybook.handybook.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Toast;

import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.R;
import com.handybook.handybook.analytics.Mixpanel;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.LaundryDropInfo;
import com.handybook.handybook.booking.model.LocalizedMonetaryAmount;
import com.handybook.handybook.booking.rating.RateImprovementConfirmationDialogFragment;
import com.handybook.handybook.booking.rating.RateImprovementDialogFragment;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.fragment.LaundryDropOffDialogFragment;
import com.handybook.handybook.booking.ui.fragment.LaundryInfoDialogFragment;
import com.handybook.handybook.booking.ui.fragment.RateServiceDialogFragment;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.NavigationManager;
import com.handybook.handybook.core.RequiredModalsEventListener;
import com.handybook.handybook.core.RequiredModalsLauncher;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.DataManagerErrorHandler;
import com.handybook.handybook.event.ActivityLifecycleEvent;
import com.handybook.handybook.module.configuration.event.ConfigurationEvent;
import com.handybook.handybook.module.notifications.splash.model.SplashPromo;
import com.handybook.handybook.module.notifications.splash.view.fragment.SplashPromoDialogFragment;
import com.handybook.handybook.ui.widget.ProgressDialog;
import com.handybook.handybook.util.FragmentUtils;
import com.squareup.otto.Bus;
import com.yozio.android.Yozio;

import java.util.ArrayList;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity implements RequiredModalsLauncher
{
    private static final String YOZIO_DEEPLINK_HOST = "deeplink.yoz.io";
    private static final String KEY_APP_LAUNDRY_INFO_SHOWN = "APP_LAUNDRY_INFO_SHOWN";
    private static final String TAG = BaseActivity.class.getName();
    protected boolean allowCallbacks;
    protected ProgressDialog mProgressDialog;
    protected Toast mToast;
    @Inject
    Mixpanel mMixpanel;
    @Inject
    protected UserManager mUserManager;
    @Inject
    DataManager mDataManager;
    @Inject
    DataManagerErrorHandler mDataManagerErrorHandler;
    @Inject
    NavigationManager mNavigationManager;
    @Inject
    Bus mBus;
    private RequiredModalsEventListener mRequiredModalsEventListener;
    private OnBackPressedListener mOnBackPressedListener;

    //Public Properties
    public boolean getAllowCallbacks()
    {
        return this.allowCallbacks;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Yozio.initialize(this);
        ((BaseApplication) this.getApplication()).inject(this);
        mRequiredModalsEventListener = new RequiredModalsEventListener(this);
        if (!BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_STAGE)
                && !BuildConfig.DEBUG)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Yozio.YOZIO_ENABLE_LOGGING = false;
        }
        final Intent intent = getIntent();
        final Uri data = intent.getData();
        if (data != null && data.getHost() != null && data.getHost().equals(YOZIO_DEEPLINK_HOST))
        {
            mMixpanel.trackEventYozioOpen(Yozio.getMetaData(intent));
        }
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setDelay(400);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.loading));
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        allowCallbacks = false;
    }

    @Override
    protected void onResumeFragments()
    {
        super.onResumeFragments();
        mBus.register(mRequiredModalsEventListener);
        mBus.post(new ActivityLifecycleEvent.FragmentsResumed(this));
        //can't put this in BaseApplication where activity lifecycle callbacks are registered
        //because this is only for FragmentActivity
    }

    @Override
    protected void onPause()
    {
        mBus.unregister(mRequiredModalsEventListener);
        super.onPause();
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        showRequiredUserModals();
    }

    @Override
    protected void onDestroy()
    {
        mMixpanel.flush();
        super.onDestroy();
    }

    @Override
    public void showBlockingScreen()
    {
        Intent launchBlockingActivity = new Intent(this, BlockingActivity.class);
        launchBlockingActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        launchBlockingActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launchBlockingActivity);
        finish();
    }

    @Override
    public void showSplashPromo(@NonNull SplashPromo splashPromo)
    {
        if (splashPromo.shouldDisplay())
        {
            SplashPromoDialogFragment splashPromoDialogFragment =
                    SplashPromoDialogFragment.newInstance(splashPromo);
            FragmentUtils.safeLaunchDialogFragment(splashPromoDialogFragment, this, SplashPromoDialogFragment.class.getSimpleName());
        }
    }

    private void showRequiredUserModals()
    {
        final FragmentManager fm = getSupportFragmentManager();
        final User user = mUserManager.getCurrentUser();
        if (user == null
                || fm.findFragmentByTag(RateServiceDialogFragment.class.getSimpleName()) != null
                || fm.findFragmentByTag(LaundryDropOffDialogFragment.class.getSimpleName()) != null
                || fm.findFragmentByTag(LaundryInfoDialogFragment.class.getSimpleName()) != null
                || fm.findFragmentByTag(RateImprovementDialogFragment.class.getSimpleName()) != null
                || fm.findFragmentByTag(RateImprovementConfirmationDialogFragment.class.getSimpleName()) != null
                || !(
                BaseActivity.this instanceof ServiceCategoriesActivity
                        || BaseActivity.this instanceof BookingDetailActivity
                        || BaseActivity.this instanceof BookingsActivity
        )
                )
        {
            return;
        }
        final String proName = getIntent().getStringExtra(BundleKeys.BOOKING_RATE_PRO_NAME);
        final String bookingId = getIntent().getStringExtra(BundleKeys.BOOKING_ID);
        if (proName != null && bookingId != null)
        {
            showProRateDialog(user, proName, Integer.parseInt(bookingId));
            getIntent().removeExtra(BundleKeys.BOOKING_RATE_PRO_NAME);
            getIntent().removeExtra(BundleKeys.BOOKING_ID);
            return;
        }
        mDataManager.getUser(user.getId(), user.getAuthToken(), new DataManager.Callback<User>()
        {
            @Override
            public void onSuccess(final User user)
            {
                if (!allowCallbacks)
                {
                    return;
                }
                final int laundryBookingId = user.getLaundryBookingId();
                final int addLaundryBookingId = user.getAddLaundryBookingId();
                final String proName = user.getBookingRatePro();
                final SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(BaseActivity.this);
                if (addLaundryBookingId > 0 && !prefs.getBoolean(KEY_APP_LAUNDRY_INFO_SHOWN, false))
                {
                    showLaundryInfoModal(addLaundryBookingId, user.getAuthToken());
                }
                else if (laundryBookingId > 0)
                {
                    showLaundryDropOffModal(
                            laundryBookingId,
                            user.getAuthToken()
                    );
                }
                else if (proName != null)
                {
                    showProRateDialog(user, proName, user.getBookingRateId());
                }
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
            }
        });
    }

    private void showProRateDialog(final User user, final String proName, final int bookingId)
    {
        final ArrayList<LocalizedMonetaryAmount> localizedMonetaryAmounts =
                user.getDefaultTipAmounts();

        RateServiceDialogFragment rateServiceDialogFragment = RateServiceDialogFragment
                .newInstance(bookingId, proName, -1, localizedMonetaryAmounts, user.getCurrencyChar());

        boolean successfullyLaunched = FragmentUtils.safeLaunchDialogFragment(
                rateServiceDialogFragment,
                BaseActivity.this,
                RateServiceDialogFragment.class.getSimpleName());
        if (successfullyLaunched)
        {
            mMixpanel.trackEventProRate(Mixpanel.ProRateEventType.SHOW, bookingId,
                    proName, 0);
        }
    }

    private void showLaundryInfoModal(final int bookingId, final String authToken)
    {
        mDataManager.getAddLaundryInfo(bookingId, authToken, new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(final Booking booking)
            {
                if (!allowCallbacks)
                {
                    return;
                }

                FragmentUtils.safeLaunchDialogFragment(
                        LaundryInfoDialogFragment.newInstance(booking),
                        BaseActivity.this,
                        LaundryInfoDialogFragment.class.getSimpleName());

            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
            }
        });
    }

    private void showLaundryDropOffModal(final int bookingId, final String authToken)
    {
        mDataManager.getLaundryScheduleInfo(bookingId, authToken,
                new DataManager.Callback<LaundryDropInfo>()
                {
                    @Override
                    public void onSuccess(final LaundryDropInfo info)
                    {
                        if (!allowCallbacks)
                        {
                            return;
                        }

                        FragmentUtils.safeLaunchDialogFragment(
                                LaundryDropOffDialogFragment.newInstance(bookingId, info),
                                BaseActivity.this,
                                LaundryDropOffDialogFragment.class.getSimpleName());
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                    }
                });
    }

    @Override
    protected final void attachBaseContext(final Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed()
    {
        if (mOnBackPressedListener != null)
        {
            mOnBackPressedListener.onBack();
        }
        else
        {
            super.onBackPressed();
        }
    }

    //Lifecycle
    @Override
    protected void onStart()
    {
        super.onStart();
        allowCallbacks = true;
        mBus.post(new ConfigurationEvent.RefreshConfiguration());
    }

    public void setOnBackPressedListener(final OnBackPressedListener onBackPressedListener)
    {
        mOnBackPressedListener = onBackPressedListener;
    }

    public interface OnBackPressedListener
    {
        void onBack();
    }
}
