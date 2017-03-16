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

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.rating.PrerateProInfo;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.ActivitySafeCallback;
import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.referral.model.ReferralDescriptor;
import com.handybook.handybook.referral.model.ReferralResponse;

import static com.handybook.handybook.ratingflow.ui.RatingFlowReferralFragment.Mode.FEEDBACK;
import static com.handybook.handybook.ratingflow.ui.RatingFlowReferralFragment.Mode.REFERRAL;

public class RatingFlowActivity extends BaseActivity {

    private static final int GOOD_PRO_RATING = 4;

    private static final int RATE_TIP_STEP = 0;
    private static final int FEEDBACK_STEP = 1;
    private static final int REFERRAL_STEP = 2;

    private Booking mBooking;
    private PrerateProInfo mPrerateProInfo;
    private ReferralDescriptor mReferralDescriptor;
    private int mCurrentStep;
    private boolean mBackPressed;
    private int mProRating;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBooking = getIntent().getParcelableExtra(BundleKeys.BOOKING);
        setContentView(R.layout.activity_rating_flow);
        startRatingFlow();
        fetchData();
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
                if (mPrerateProInfo != null) {
                    fragment = RatingFlowFeedbackFragment.newInstance(
                            mBooking,
                            mPrerateProInfo,
                            mProRating
                    );
                }
                break;
            case REFERRAL_STEP:
                if (mReferralDescriptor != null) {
                    fragment = RatingFlowReferralFragment.newInstance(
                            mBooking,
                            mProRating < GOOD_PRO_RATING ? FEEDBACK : REFERRAL,
                            mReferralDescriptor
                    );
                }
                break;
            default:
                finish();
                return;
        }
        displayFragmentOrSkip(fragment);
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

    public void finishStepWithProRating(final int proRating) {
        mProRating = proRating;
        finishStep();
    }

    public void finishStep() {
        mCurrentStep++;
        continueRatingFlow();
    }

    private void fetchData() {
        mDataManager.requestPrerateProInfo(
                mBooking.getId(),
                new ActivitySafeCallback<PrerateProInfo, BaseActivity>(this) {

                    @Override
                    public void onCallbackSuccess(final PrerateProInfo prerateProInfo) {
                        mPrerateProInfo = prerateProInfo;
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        // do nothing
                    }
                }
        );
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
                });
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
