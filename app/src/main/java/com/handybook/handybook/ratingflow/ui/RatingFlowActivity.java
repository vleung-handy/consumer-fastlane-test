package com.handybook.handybook.ratingflow.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.transition.AutoTransition;
import android.transition.Fade;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.booking.rating.PrerateProInfo;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.HandyRetrofitService;
import com.handybook.handybook.core.data.callback.ActivitySafeCallback;
import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.proteam.model.RecommendedProvidersWrapper;
import com.handybook.handybook.referral.model.ReferralDescriptor;
import com.handybook.handybook.referral.model.ReferralResponse;
import com.handybook.handybook.vegas.VegasManager;
import com.handybook.handybook.vegas.logging.VegasLog;
import com.handybook.handybook.vegas.model.RewardsWrapper;
import com.handybook.handybook.vegas.model.VegasGame;
import com.handybook.handybook.vegas.ui.VegasActivity;
import com.squareup.otto.Bus;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.handybook.handybook.ratingflow.ui.RatingFlowReferralFragment.Mode.FEEDBACK;
import static com.handybook.handybook.ratingflow.ui.RatingFlowReferralFragment.Mode.REFERRAL;

public class RatingFlowActivity extends BaseActivity {

    @Inject
    HandyRetrofitService mService;

    @Inject
    VegasManager mVegasManager;

    @Inject
    Bus mBus;

    private static final int EXCELLENT_PRO_RATING = 5;

    private static final int RATE_TIP_STEP = 0;
    private static final int FEEDBACK_STEP = 1;
    private static final int REFERRAL_STEP = 2;
    private static final int GAME_STEP = 3;

    private Booking mBooking;
    private PrerateProInfo mPrerateProInfo;
    private ReferralDescriptor mReferralDescriptor;
    private int mCurrentStep;
    private boolean mBackPressed;
    private int mProRating;
    private ProviderMatchPreference mMatchPreference;
    private ArrayList<Provider> mRecommendedProviders;
    private boolean mIsFetchingRecommendedProviders = false;
    private VegasGame mVegasGame;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBooking = getIntent().getParcelableExtra(BundleKeys.BOOKING);
        setContentView(R.layout.activity_rating_flow);
        startRatingFlow();
        fetchPrerateInfo();
        fetchReferralInfo();
    }

    private void startRatingFlow() {
        mCurrentStep = 0;
        continueRatingFlow();
    }

    private synchronized void continueRatingFlow() {
        // Expect this to stay null if data isn't available for the fragment.
        Fragment fragment = null;

        switch (mCurrentStep) {
            case RATE_TIP_STEP:
                fragment = RatingFlowRateAndTipFragment.newInstance(mBooking);
                break;
            case FEEDBACK_STEP:
                fetchRewardInfo();
                if (mPrerateProInfo != null) {
                    fragment = RatingFlowFeedbackFragment.newInstance(
                            mBooking,
                            mPrerateProInfo,
                            mReferralDescriptor,
                            mMatchPreference,
                            mProRating
                    );
                }
                break;
            case REFERRAL_STEP:
                if (shouldSkipReferralStep()) {
                    finishStep();
                    return;
                }
                fragment = RatingFlowReferralFragment.newInstance(
                        mBooking,
                        shouldShowRecommendedProviders() ? FEEDBACK : REFERRAL,
                        mReferralDescriptor,
                        mRecommendedProviders
                );
                break;
            case GAME_STEP:
                if (mVegasGame == null) {
                    finish();
                    return;
                }
                startActivity(VegasActivity.getIntent(this, mVegasGame));
                finish();
                return;
            default:
                finish();
                return;
        }
        displayFragmentOrSkip(fragment);
    }

    private boolean shouldSkipReferralStep() {
        Fragment activeFragment
                = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (activeFragment instanceof RatingFlowFeedbackFragment) {
            if (((RatingFlowFeedbackFragment) activeFragment).getActiveFragment() instanceof RatingFlowShareProFragment) {
                return true;
            }
        }
        return false;
    }

    private void displayFragmentOrSkip(@Nullable final Fragment fragment) {
        if (fragment != null) {
            final FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack();

            addSharedElements(fragmentTransaction, fragment);

            fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
        }
        else {
            finishStep();
        }
    }

    private void addSharedElements(
            final FragmentTransaction fragmentTransaction,
            final Fragment targetFragment
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (targetFragment instanceof RatingFlowReferralFragment) {
                targetFragment.setEnterTransition(new Fade(Fade.IN));
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Fragment currentFragment
                    = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof RatingFlowRateAndTipFragment
                && targetFragment instanceof RatingFlowFeedbackFragment) {
                final View view = currentFragment.getView();
                if (view == null) { return; }
                final ImageView proImage
                        = (ImageView) view.findViewById(R.id.rating_flow_pro_image);
                final RatingFlowFiveStarsView stars
                        = (RatingFlowFiveStarsView) view.findViewById(R.id.rating_flow_stars);
                final View divider = view.findViewById(R.id.rating_flow_divider);
                if (proImage == null || stars == null || divider == null) { return; }
                targetFragment.setSharedElementEnterTransition(
                        new AutoTransition().setInterpolator(new DecelerateInterpolator()));
                fragmentTransaction.addSharedElement(
                        proImage,
                        getString(R.string.transition_pro_image)
                );
                fragmentTransaction.addSharedElement(
                        divider,
                        getString(R.string.transition_divider)
                );
                fragmentTransaction.addSharedElement(stars, getString(R.string.transition_stars));
            }
        }
    }

    public void finishStepWithProRatingAndMatchPreference(
            final int proRating,
            final ProviderMatchPreference matchPreference
    ) {
        mProRating = proRating;
        mMatchPreference = matchPreference;
        fetchRecommendedProvidersIfNecessary();
        finishStep();
    }

    public void finishStep() {
        mCurrentStep++;
        continueRatingFlow();
    }

    private void fetchReferralInfo() {
        mDataManager.requestPrepareReferrals(
                false,
                new ActivitySafeCallback<ReferralResponse, BaseActivity>(this) {
                    @Override
                    public void onCallbackSuccess(final ReferralResponse response) {
                        mReferralDescriptor = response.getReferralDescriptor();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        // do nothing
                    }
                }
        );
    }

    private void fetchPrerateInfo() {
        mDataManager.requestPrerateProInfo(
                mBooking.getId(),
                new ActivitySafeCallback<PrerateProInfo, BaseActivity>(this) {

                    @Override
                    public void onCallbackSuccess(final PrerateProInfo prerateProInfo) {
                        mPrerateProInfo = prerateProInfo;

                        //if provider information is not there, then we can put it there.
                        if (mPrerateProInfo.getProReferralInfo() != null
                            && mPrerateProInfo.getProReferralInfo().getProvider() == null
                            && mBooking.getProvider() != null) {
                            mPrerateProInfo.getProReferralInfo()
                                           .setProvider(mBooking.getProvider());
                        }
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        // do nothing
                    }
                }
        );
    }

    private void fetchRecommendedProviders() {
        mDataManager.getRecommendedProviders(
                mUserManager.getCurrentUser().getId(),
                mBooking.getService().getId(),
                new ActivitySafeCallback<RecommendedProvidersWrapper, RatingFlowActivity>(this) {
                    @Override
                    public void onCallbackSuccess(final RecommendedProvidersWrapper response) {
                        mRecommendedProviders = response.getRecommendedProviders();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        // do nothing
                    }
                }
        );
    }

    private void fetchRewardInfo() {
        mBus.post(new LogEvent.AddLogEvent(new VegasLog.GameRequestSubmitted()));
        mVegasManager.getReward(
                new ActivitySafeCallback<RewardsWrapper, RatingFlowActivity>(this) {
                    @Override
                    public void onCallbackSuccess(final RewardsWrapper response) {
                        mBus.post(new LogEvent.AddLogEvent(new VegasLog.GameRequestSuccess()));
                        final VegasGame[] games = response.games;
                        if (games == null) {
                            return;
                        }
                        for (VegasGame game : games) {
                            if (game.isValid()) {
                                mVegasGame = game;
                                break; // First game we can show goes
                            }
                        }
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        mBus.post(new LogEvent.AddLogEvent(
                                new VegasLog.GameRequestError(error.getMessage())
                        ));
                        Crashlytics.log("Failed to fetch rewardInfo!");
                    }
                }
        );
    }

    private synchronized void fetchRecommendedProvidersIfNecessary() {
        if (mRecommendedProviders == null
            && !mIsFetchingRecommendedProviders
            && shouldShowRecommendedProviders()) {
            fetchRecommendedProviders();
            mIsFetchingRecommendedProviders = true;
        }
    }

    private boolean shouldShowRecommendedProviders() {
        return (mProRating > 0 && mProRating < EXCELLENT_PRO_RATING)
               || mMatchPreference == ProviderMatchPreference.NEVER;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mUserManager.isUserLoggedIn()) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed) {
            super.onBackPressed();
        }
        else {
            final Toast toast = Toast.makeText(this, R.string.press_back_again, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            mBackPressed = true;
        }
    }
}
