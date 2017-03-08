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
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RatingFlowFeedbackFragment extends InjectedFragment {

    private static final int START_DELAY_MILLIS = 400;
    private static final int GOOD_PRO_RATING = 4;


    private enum Step {
        MATCH_PREFERENCE, IMPROVEMENT, REVIEW
    }


    @Bind(R.id.rating_flow_pro_image)
    ImageView mProImage;
    @Bind(R.id.rating_flow_stars)
    RatingFlowFiveStarsView mStars;
    @Bind(R.id.rating_flow_next_button)
    Button mNextButton;

    private Booking mBooking;
    private PrerateProInfo mPrerateProInfo;
    private int mProRating;
    private Step mCurrentStep;

    @NonNull
    public static RatingFlowFeedbackFragment newInstance(
            @NonNull final Booking booking,
            @NonNull final PrerateProInfo prerateProInfo,
            final int proRating
    ) {

        final RatingFlowFeedbackFragment fragment = new RatingFlowFeedbackFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BundleKeys.BOOKING, booking);
        arguments.putSerializable(BundleKeys.PRERATE_PRO_INFO, prerateProInfo);
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
            ((RatingFlowFeedbackChildFragment) fragment).onSubmit();
        }
    }

    @OnClick(R.id.rating_flow_skip_button)
    public void onSkipClicked() {
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
                return mProRating >= GOOD_PRO_RATING;
            case IMPROVEMENT:
                return mProRating < GOOD_PRO_RATING;
            case REVIEW:
                return mProRating >= GOOD_PRO_RATING;
            default:
                return false;
        }
    }

    @Nullable
    private RatingFlowFeedbackChildFragment createFragmentForStep(@NonNull Step step) {
        switch (step) {
            case MATCH_PREFERENCE:
                return RatingFlowMatchPreferenceFragment.newInstance(mBooking.getProvider());
            case IMPROVEMENT:
                return RatingFlowImprovementFragment.newInstance(
                        Lists.newArrayList(mPrerateProInfo.getReasons()),
                        new RateImprovementFeedback(mBooking.getId())
                );
            case REVIEW:
                return RatingFlowReviewFragment.newInstance(mBooking);
            default:
                return null;
        }
    }
}
