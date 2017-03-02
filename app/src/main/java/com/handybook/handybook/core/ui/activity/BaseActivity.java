package com.handybook.handybook.core.ui.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

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
import com.handybook.handybook.bottomnav.BottomNavActivity;
import com.handybook.handybook.configuration.manager.ConfigurationManager;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.NavigationManager;
import com.handybook.handybook.core.RequiredModalsEventListener;
import com.handybook.handybook.core.RequiredModalsLauncher;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.DataManagerErrorHandler;
import com.handybook.handybook.core.data.callback.ActivitySafeCallback;
import com.handybook.handybook.core.event.ActivityLifecycleEvent;
import com.handybook.handybook.core.manager.AppseeManager;
import com.handybook.handybook.core.manager.DefaultPreferencesManager;
import com.handybook.handybook.core.manager.SessionManager;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.AppLog;
import com.handybook.handybook.promos.splash.SplashPromo;
import com.handybook.handybook.promos.splash.SplashPromoDialogFragment;
import com.handybook.handybook.ratingflow.ui.RatingFlowActivity;

import com.handybook.handybook.referral.manager.ReferralsManager;
import com.handybook.handybook.referral.model.ReferralResponse;
import com.squareup.otto.Bus;
import com.yozio.android.Yozio;

import java.util.ArrayList;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity implements RequiredModalsLauncher {

    private static final String YOZIO_DEEPLINK_HOST = "deeplink.yoz.io";
    private static final String TAG = BaseActivity.class.getName();
    protected boolean allowCallbacks;

    @Inject
    protected UserManager mUserManager;
    @Inject
    protected DefaultPreferencesManager mDefaultPreferencesManager;
    @Inject
    protected DataManager mDataManager;
    @Inject
    protected ConfigurationManager mConfigurationManager;
    @Inject
    protected SessionManager mSessionManager;
    @Inject
    DataManagerErrorHandler mDataManagerErrorHandler;
    @Inject
    NavigationManager mNavigationManager;
    @Inject
    AppseeManager mAppseeManager;
    @Inject
    protected Bus mBus;

    private RequiredModalsEventListener mRequiredModalsEventListener;
    private OnBackPressedListener mOnBackPressedListener;
    private boolean mWasOpenBefore;
    private boolean mRateDialogShown;

    //Public Properties
    public boolean getAllowCallbacks() {
        return this.allowCallbacks;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Yozio.initialize(this);
        ((BaseApplication) this.getApplication()).inject(this);
        mRequiredModalsEventListener = new RequiredModalsEventListener(this);
        if (!BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_STAGE)
            && !BuildConfig.DEBUG) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Yozio.YOZIO_ENABLE_LOGGING = false;
        }
    }

    /**
     * If the new onboarding flag is enabled, and we don't have zip or email, then
     * user is required to go to onboarding screen.
     *
     * Do this only if the user is not currently logged in
     * @return
     */
    protected boolean requiresOnboardingV2(Configuration config) {
        return config.isOnboardingV2Enabled()
               && !mUserManager.isUserLoggedIn() &&
               (TextUtils.isEmpty(mDefaultPreferencesManager.getString(PrefsKey.ZIP, null))
                || TextUtils.isEmpty(mDefaultPreferencesManager.getString(
                       PrefsKey.EMAIL,
                       null
               ))
               );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSessionManager.markActivity();

        if (mWasOpenBefore) {
            mBus.post(new LogEvent.AddLogEvent(new AppLog.AppOpenLog(false, false)));
        }
        /*
        start/stop screen recording
        according to docs, this call should ONLY be made from Activity.onCreate() or Activity.onResume()

        although it is only necessary to start Appsee in activities that are entry points to the app
        (SplashActivity, OldDeeplinkActivity), putting this here because
        we may want to start/stop recording when configs or storage space change

        note that because config response isn't guaranteed at this point,
        recording might not start until the next call of onResume() in which configs are present.
        consider refactoring the way we deal with configs so that it's similar to portal

        NOTE: For emulators, it's not supported so you may see a NullPointerException: APPSEE_NO_CONTEXT
         */
        mAppseeManager.startOrStopRecordingAsNecessary();
    }

    @Override
    protected void onStop() {
        super.onStop();
        allowCallbacks = false;
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        mBus.register(mRequiredModalsEventListener);
        mBus.post(new ActivityLifecycleEvent.FragmentsResumed(this));
        //can't put this in BaseApplication where activity lifecycle callbacks are registered
        //because this is only for FragmentActivity
    }

    @Override
    protected void onPause() {
        mWasOpenBefore = true;
        mBus.unregister(mRequiredModalsEventListener);
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        showRequiredUserModals();
    }

    @Override
    public void showBlockingScreen() {
        Intent launchBlockingActivity = new Intent(this, BlockingActivity.class);
        launchBlockingActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        launchBlockingActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launchBlockingActivity);
        finish();
    }

    @Override
    public void showSplashPromo(@NonNull SplashPromo splashPromo) {
        if (splashPromo.shouldDisplay()) {
            SplashPromoDialogFragment splashPromoDialogFragment =
                    SplashPromoDialogFragment.newInstance(splashPromo);
            FragmentUtils.safeLaunchDialogFragment(
                    splashPromoDialogFragment,
                    this,
                    SplashPromoDialogFragment.class.getSimpleName()
            );
        }
    }

    @Override
    public void showReferralDialog(
            final ReferralResponse referralResponse,
            final ReferralsManager.Source source
    ) {
        if (getSupportFragmentManager().findFragmentByTag(ReferralDialogFragment.TAG) == null) {
            final ReferralDialogFragment dialogFragment =
                    ReferralDialogFragment.newInstance(
                            referralResponse.getReferralDescriptor(),
                            source
                    );
            FragmentUtils.safeLaunchDialogFragment(dialogFragment, this,
                                                   ReferralDialogFragment.TAG
            );
        }
    }

    private void showRequiredUserModals() {
        final FragmentManager fm = getSupportFragmentManager();
        final User user = mUserManager.getCurrentUser();
        if (user == null
            || fm.findFragmentByTag(RateServiceDialogFragment.class.getSimpleName()) != null
            || fm.findFragmentByTag(LaundryDropOffDialogFragment.class.getSimpleName()) != null
            || fm.findFragmentByTag(LaundryInfoDialogFragment.class.getSimpleName()) != null
            || fm.findFragmentByTag(RateImprovementDialogFragment.class.getSimpleName()) != null
            ||
            fm.findFragmentByTag(RateImprovementConfirmationDialogFragment.class.getSimpleName()) !=
            null
            || fm.findFragmentByTag(ReferralDialogFragment.TAG) != null
            || !(BaseActivity.this instanceof ServiceCategoriesActivity
                 || BaseActivity.this instanceof BookingDetailActivity
                 || BaseActivity.this instanceof BookingsActivity
                 || BaseActivity.this instanceof BottomNavActivity)
                ) {
            return;
        }
        final String proName = getIntent().getStringExtra(BundleKeys.BOOKING_RATE_PRO_NAME);
        final String bookingId = getIntent().getStringExtra(BundleKeys.BOOKING_ID);
        if (proName != null && bookingId != null) {
            showProRateDialog(user, proName, Integer.parseInt(bookingId));
            getIntent().removeExtra(BundleKeys.BOOKING_RATE_PRO_NAME);
            getIntent().removeExtra(BundleKeys.BOOKING_ID);
            return;
        }
        mDataManager.getUser(user.getId(), user.getAuthToken(), new DataManager.Callback<User>() {
            @Override
            public void onSuccess(final User user) {
                if (!allowCallbacks) {
                    return;
                }
                final int laundryBookingId = user.getLaundryBookingId();
                final int addLaundryBookingId = user.getAddLaundryBookingId();
                final String proName = user.getBookingRatePro();
                if (addLaundryBookingId > 0 &&
                    !mDefaultPreferencesManager.getBoolean(
                            PrefsKey.APP_LAUNDRY_INFO_SHOWN,
                            false
                    )) {
                    showLaundryInfoModal(addLaundryBookingId);
                }
                else if (laundryBookingId > 0) {
                    showLaundryDropOffModal(
                            laundryBookingId
                    );
                }
                else if (proName != null) {
                    showProRateDialog(user, proName, user.getBookingRateId());
                }
            }

            @Override
            public void onError(DataManager.DataManagerError error) {}
        });
    }

    private void showProRateDialog(final User user, final String proName, final int bookingId) {
        final ArrayList<LocalizedMonetaryAmount> localizedMonetaryAmounts =
                user.getDefaultTipAmounts();
        if (mConfigurationManager.getPersistentConfiguration().isNewRatingFlowEnabled()) {
            mDataManager.getBooking(
                    String.valueOf(bookingId),
                    new ActivitySafeCallback<Booking, BaseActivity>(this) {
                        @Override
                        public void onCallbackSuccess(final Booking booking) {
                            launchRatingFlowActivity(booking);
                        }

                        @Override
                        public void onCallbackError(final DataManager.DataManagerError error) {
                            // do nothing
                        }
                    }
            );
        }
        else {
            RateServiceDialogFragment rateServiceDialogFragment = RateServiceDialogFragment
                    .newInstance(
                            bookingId,
                            proName,
                            -1,
                            localizedMonetaryAmounts,
                            user.getCurrencyChar()
                    );
            FragmentUtils.safeLaunchDialogFragment(
                    rateServiceDialogFragment,
                    BaseActivity.this,
                    RateServiceDialogFragment.class.getSimpleName()
            );
        }
    }

    private void launchRatingFlowActivity(final Booking booking) {
        final Intent intent = new Intent(BaseActivity.this, RatingFlowActivity.class);
        intent.putExtra(BundleKeys.BOOKING, booking);
        startActivity(intent);
    }

    private void showLaundryInfoModal(final int bookingId) {
        mDataManager.getAddLaundryInfo(bookingId, new DataManager.Callback<Booking>() {
            @Override
            public void onSuccess(final Booking booking) {
                if (!allowCallbacks) {
                    return;
                }

                FragmentUtils.safeLaunchDialogFragment(
                        LaundryInfoDialogFragment.newInstance(booking),
                        BaseActivity.this,
                        LaundryInfoDialogFragment.class.getSimpleName()
                );

            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
            }
        });
    }

    private void showLaundryDropOffModal(final int bookingId) {
        mDataManager.getLaundryScheduleInfo(
                bookingId,
                new DataManager.Callback<LaundryDropInfo>() {
                    @Override
                    public void onSuccess(final LaundryDropInfo info) {
                        if (!allowCallbacks) {
                            return;
                        }

                        FragmentUtils.safeLaunchDialogFragment(
                                LaundryDropOffDialogFragment.newInstance(bookingId, info),
                                BaseActivity.this,
                                LaundryDropOffDialogFragment.class.getSimpleName()
                        );
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
                    }
                }
        );
    }

    @Override
    protected final void attachBaseContext(final Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        if (mOnBackPressedListener != null) {
            mOnBackPressedListener.onBack();
        }
        else {
            super.onBackPressed();
        }
    }

    //Lifecycle
    @Override
    protected void onStart() {
        super.onStart();
        allowCallbacks = true;
    }

    public void setOnBackPressedListener(final OnBackPressedListener onBackPressedListener) {
        mOnBackPressedListener = onBackPressedListener;
    }

    public interface OnBackPressedListener {

        void onBack();
    }

    @Nullable
    public static BaseActivity getInstance(Context context) {
        if (context == null) { return null; }
        else if (context instanceof BaseActivity) { return (BaseActivity) context; }
        else if (context instanceof ContextWrapper) {
            return getInstance(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }
}
