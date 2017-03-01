package com.handybook.handybook.ratingflow.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.transition.AutoTransition;
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

public class RatingFlowActivity extends BaseActivity {

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

    // TODO: Solve edge case where data isn't ready by the time the step is ready to execute
    private synchronized void continueRatingFlow() {
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
            default:
                finish();
                return;
        }
        if (fragment != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fragment.setSharedElementEnterTransition(new AutoTransition().setInterpolator(
                        new DecelerateInterpolator()));
            }

            final FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack();

            addSharedElements(fragmentTransaction);

            fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
        }
    }

    private void addSharedElements(final FragmentTransaction fragmentTransaction) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Fragment currentFragment
                    = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof RatingFlowRateAndTipFragment) {
                final View view = currentFragment.getView();
                if (view == null) { return; }
                final ImageView proImage
                        = (ImageView) view.findViewById(R.id.rating_flow_pro_image);
                final RatingFlowFiveStarsView stars
                        = (RatingFlowFiveStarsView) view.findViewById(R.id.rating_flow_stars);
                final View divider = view.findViewById(R.id.rating_flow_divider);
                if (proImage == null || stars == null || divider == null) { return; }
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
