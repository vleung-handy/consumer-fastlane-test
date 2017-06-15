package com.handybook.handybook.ratingflow.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.common.collect.Lists;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.rating.PrerateProInfo;
import com.handybook.handybook.booking.rating.RateImprovementFeedback;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.proteam.model.ProTeamEdit;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.referral.model.ReferralDescriptor;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.handybook.handybook.proteam.model.ProviderMatchPreference.FAVORITE;
import static com.handybook.handybook.proteam.model.ProviderMatchPreference.NEVER;
import static com.handybook.handybook.proteam.model.ProviderMatchPreference.PREFERRED;

public class RatingFlowFeedbackFragment extends InjectedFragment {

    private static final int START_DELAY_MILLIS = 400;
    private static final int GOOD_PRO_RATING = 4;


    private enum Step {
        MATCH_PREFERENCE, IMPROVEMENT, REVIEW_PROVIDER, SHARE_PROVIDER
    }


    @BindView(R.id.rating_flow_pro_image)
    ImageView mProImage;
    @BindView(R.id.rating_flow_stars)
    RatingFlowFiveStarsView mStars;
    @BindView(R.id.rating_flow_next_button)
    Button mNextButton;

    private Booking mBooking;
    private PrerateProInfo mPrerateProInfo;
    private ReferralDescriptor mReferralDescriptor;
    private int mProRating;
    private ProviderMatchPreference mSelectedPreference;
    private Step mCurrentStep;

    @NonNull
    public static RatingFlowFeedbackFragment newInstance(
            @NonNull final Booking booking,
            @NonNull final PrerateProInfo prerateProInfo,
            @NonNull final ReferralDescriptor referralDescriptor,
            final int proRating
    ) {
        final RatingFlowFeedbackFragment fragment = new RatingFlowFeedbackFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BundleKeys.BOOKING, booking);
        arguments.putSerializable(BundleKeys.PRERATE_PRO_INFO, prerateProInfo);
        arguments.putSerializable(BundleKeys.REFERRAL_DESCRIPTOR, referralDescriptor);
        arguments.putInt(BundleKeys.PRO_RATING, proRating);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
        mPrerateProInfo =
                (PrerateProInfo) getArguments().getSerializable(BundleKeys.PRERATE_PRO_INFO);
        mReferralDescriptor =
                (ReferralDescriptor) getArguments().getSerializable(BundleKeys.REFERRAL_DESCRIPTOR);

        mProRating = getArguments().getInt(BundleKeys.PRO_RATING);
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(
                R.layout.fragment_rating_flow_feedback,
                container,
                false
        );
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        initHeader();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                continueFeedbackFlow();
            }
        }, START_DELAY_MILLIS);
    }

    private void initHeader() {
        Picasso.with(getContext())
               .load(mBooking.getProvider().getImageUrl())
               .placeholder(R.drawable.img_pro_placeholder)
               .noFade()
               .into(mProImage);
        mStars.selectRating(mProRating);
        mStars.lock();
    }

    @OnClick(R.id.rating_flow_next_button)
    public void onNextClicked() {
        final Fragment fragment
                = getChildFragmentManager().findFragmentById(R.id.rating_flow_feedback_content);
        if (fragment != null && fragment instanceof RatingFlowFeedbackChildFragment) {

            if (fragment instanceof RatingFlowMatchPreferenceFragment) {
                mSelectedPreference
                        = ((RatingFlowMatchPreferenceFragment) fragment).getSelectedPreference();
            }

            ((RatingFlowFeedbackChildFragment) fragment).onSubmit();
        }
    }

    @OnClick(R.id.rating_flow_skip_button)
    public void onSkipClicked() {
        Fragment activeFragment = getActiveFragment();
        if (activeFragment != null && activeFragment instanceof RatingFlowFeedbackChildFragment) {
            ((RatingFlowFeedbackChildFragment) activeFragment).onSkip();
        }

        continueFeedbackFlow();
    }

    void continueFeedbackFlow() {
        final Step nextStep = resolveNextStep();
        if (nextStep != null) {
            final RatingFlowFeedbackChildFragment fragment = createFragmentForStep(nextStep);
            if (fragment != null) {
                showFragment(fragment);
            }
            else {
                continueFeedbackFlow();
            }
        }
        else {
            if (getActivity() instanceof RatingFlowActivity) {
                ((RatingFlowActivity) getActivity()).finishStep();
            }
        }
    }

    void continueFeedbackFlowWithProTeamEditRequest(final ProTeamEdit proTeamEdit) {
        if (getActivity() instanceof RatingFlowActivity) {
            ((RatingFlowActivity) getActivity()).requestProTeamEdit(proTeamEdit);
        }
        continueFeedbackFlow();
    }

    void showFragment(final RatingFlowFeedbackChildFragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.fade_slide_in_right, R.anim.fade_slide_out_left)
                .replace(R.id.rating_flow_feedback_content, fragment)
                .commit();
    }

    // This method is recursive. It will keep resolving the next displayable step until it becomes
    // null. This means that the flow is finished once it returns null. However, if the current step
    // is null and this method gets called, it assigns the first step of the flow to the current
    // step, assuming that the flow is just starting.
    @Nullable
    private Step resolveNextStep() {
        if (mCurrentStep == null) {
            mCurrentStep = Step.values()[0];
        }
        else {
            final int nextStepOrdinal = mCurrentStep.ordinal() + 1;
            if (nextStepOrdinal < Step.values().length) {
                mCurrentStep = Step.values()[nextStepOrdinal];
            }
            else {
                return (mCurrentStep = null);
            }
        }
        return shouldDisplayStep(mCurrentStep) ? mCurrentStep : resolveNextStep();
    }

    private boolean shouldDisplayStep(@NonNull final Step step) {
        switch (step) {
            case MATCH_PREFERENCE:
            case REVIEW_PROVIDER:
                return true;
            case IMPROVEMENT:
                return mProRating < GOOD_PRO_RATING;
            case SHARE_PROVIDER:
                //should return true when it's a high rating, has pro information, and user
                //elected to work with pro again.
                return mProRating >= GOOD_PRO_RATING &&
                       mPrerateProInfo.getProReferralInfo() != null &&
                       (mSelectedPreference == PREFERRED ||
                        mSelectedPreference == FAVORITE);
            default:
                return false;
        }
    }

    @Nullable
    private RatingFlowFeedbackChildFragment createFragmentForStep(@NonNull Step step) {
        switch (step) {
            case MATCH_PREFERENCE:
                return RatingFlowMatchPreferenceFragment.newInstance(
                        mBooking,
                        mProRating >= GOOD_PRO_RATING ? PREFERRED : NEVER
                );
            case IMPROVEMENT:
                return RatingFlowImprovementFragment.newInstance(
                        Lists.newArrayList(mPrerateProInfo.getReasons()),
                        new RateImprovementFeedback(mBooking.getId())
                );
            case REVIEW_PROVIDER:
                return RatingFlowReviewFragment.newInstance(mBooking);
            case SHARE_PROVIDER:
                return RatingFlowShareProFragment.newInstance(
                        mPrerateProInfo.getProReferralInfo(),
                        mReferralDescriptor,
                        mBooking.getProvider()
                );

            default:
                return null;
        }
    }

    /**
     * Returns the fragment that is active on the screen.
     * @return
     */
    @Nullable
    public Fragment getActiveFragment() {
        return getChildFragmentManager().findFragmentById(R.id.rating_flow_feedback_content);
    }
}
