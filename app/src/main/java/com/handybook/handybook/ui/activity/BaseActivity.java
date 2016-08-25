package com.handybook.handybook.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Toast;

import com.handybook.handybook.BuildConfig;
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
import com.handybook.handybook.booking.ui.fragment.ReferralDialogFragment;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.NavigationManager;
import com.handybook.handybook.core.RequiredModalsEventListener;
import com.handybook.handybook.core.RequiredModalsLauncher;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.DataManagerErrorHandler;
import com.handybook.handybook.event.ActivityLifecycleEvent;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.AppLog;
import com.handybook.handybook.manager.PrefsManager;
import com.handybook.handybook.module.configuration.manager.ConfigurationManager;
import com.handybook.handybook.module.notifications.splash.model.SplashPromo;
import com.handybook.handybook.module.notifications.splash.view.fragment.SplashPromoDialogFragment;
import com.handybook.handybook.module.referral.model.ReferralResponse;
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
    protected Toast mToast;

    @Inject
    PrefsManager mPrefsManager;
    @Inject
    protected UserManager mUserManager;
    @Inject
    DataManager mDataManager;
    @Inject
    ConfigurationManager mConfigurationManager;
    @Inject
    DataManagerErrorHandler mDataManagerErrorHandler;
    @Inject
    NavigationManager mNavigationManager;
    @Inject
    protected Bus mBus;

    private RequiredModalsEventListener mRequiredModalsEventListener;
    private OnBackPressedListener mOnBackPressedListener;
    private static final long APP_OPEN_TIMEOUT = 1800000; // 30 mins
    private static long timeSinceLastLogSent = 0;

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
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (timeSinceLastLogSent == 0 || (System.currentTimeMillis() - timeSinceLastLogSent >= APP_OPEN_TIMEOUT))
        {
            timeSinceLastLogSent = System.currentTimeMillis();
            if (mPrefsManager.getBoolean(PrefsKey.APP_FIRST_LAUNCH, true))
            {
                mBus.post(new LogEvent.AddLogEvent(new AppLog.AppOpenLog(true)));
                mPrefsManager.setBoolean(PrefsKey.APP_FIRST_LAUNCH, false);
            }
            else
            {
                mBus.post(new LogEvent.AddLogEvent(new AppLog.AppOpenLog(false)));
            }
        }
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

    @Override
    public void showReferralDialog(final ReferralResponse referralResponse)
    {
        if (getSupportFragmentManager().findFragmentByTag(ReferralDialogFragment.TAG) == null)
        {
            final ReferralDialogFragment dialogFragment =
                    ReferralDialogFragment.newInstance(referralResponse.getReferralDescriptor());
            FragmentUtils.safeLaunchDialogFragment(dialogFragment, this,
                    ReferralDialogFragment.TAG);
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
                || fm.findFragmentByTag(ReferralDialogFragment.TAG) != null
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
                    showLaundryInfoModal(addLaundryBookingId);
                }
                else if (laundryBookingId > 0)
                {
                    showLaundryDropOffModal(
                            laundryBookingId
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

        FragmentUtils.safeLaunchDialogFragment(
                rateServiceDialogFragment,
                BaseActivity.this,
                RateServiceDialogFragment.class.getSimpleName());
    }

    private void showLaundryInfoModal(final int bookingId)
    {
        mDataManager.getAddLaundryInfo(bookingId, new DataManager.Callback<Booking>()
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

    private void showLaundryDropOffModal(final int bookingId)
    {
        mDataManager.getLaundryScheduleInfo(bookingId,
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
