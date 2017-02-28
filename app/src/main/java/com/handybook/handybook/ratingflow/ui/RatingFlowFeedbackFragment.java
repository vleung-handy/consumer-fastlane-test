package com.handybook.handybook.ratingflow.ui;

import android.app.Activity;
import android.content.Intent;
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
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.constant.RequestCode;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.HandyRetrofitCallback;
import com.handybook.handybook.core.data.HandyRetrofitService;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.proteam.event.ProTeamEvent;
import com.handybook.handybook.proteam.model.ProTeamEdit;
import com.handybook.handybook.proteam.model.ProTeamEditWrapper;
import com.handybook.handybook.proteam.model.ProTeamWrapper;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RatingFlowFeedbackFragment extends InjectedFragment {

    private static final int START_DELAY_MILLIS = 400;


    private enum Step {
        MATCH_PREFERENCE
    }


    @Inject
    HandyRetrofitService mService;

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
    private ProviderMatchPreference mSelectedPreference;

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
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case RequestCode.SELECT_MATCH_PREFERENCE:
                    mNextButton.setEnabled(true);
                    mSelectedPreference = (ProviderMatchPreference)
                            data.getSerializableExtra(BundleKeys.MATCH_PREFERENCE);
                    break;
            }
        }
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

    @OnClick(R.id.rating_flow_next_button)
    public void onNextClicked() {
        switch (mCurrentStep) {
            case MATCH_PREFERENCE:
                processMatchPreference();
                break;
        }
    }

    private void processMatchPreference() {
        showUiBlockers();
        final DataManager.Callback<ProTeamWrapper> cb =
                new FragmentSafeCallback<ProTeamWrapper>(this) {
                    @Override
                    public void onCallbackSuccess(final ProTeamWrapper proTeamWrapper) {
                        removeUiBlockers();
                        continueFeedbackFlow();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        removeUiBlockers();
                        showToast(R.string.default_error_string);
                    }
                };
        final ProTeamEdit proTeamEdit = new ProTeamEdit(mSelectedPreference);
        proTeamEdit.addId(
                Integer.parseInt(mBooking.getProvider().getId()),
                mBooking.getProvider().getCategoryType()
        );
        mService.editProTeam(
                userManager.getCurrentUser().getId(),
                new ProTeamEditWrapper(
                        Lists.newArrayList(proTeamEdit),
                        ProTeamEvent.Source.PRO_MANAGEMENT.toString()
                ),
                new HandyRetrofitCallback(cb) {
                    @Override
                    protected void success(final JSONObject response) {
                        cb.onSuccess(ProTeamWrapper.fromJson(response.toString()));
                    }
                }
        );
    }

    private void continueFeedbackFlow() {
        int nextStepOrdinal = mCurrentStep == null ? 0 : mCurrentStep.ordinal() + 1;
        if (nextStepOrdinal < Step.values().length) {
            mCurrentStep = Step.values()[nextStepOrdinal];

            Fragment fragment = null;
            switch (mCurrentStep) {
                case MATCH_PREFERENCE:
                    mNextButton.setEnabled(false);
                    fragment = RatingFlowMatchPreferenceFragment
                            .newInstance(mBooking.getProvider());
                    fragment.setTargetFragment(this, RequestCode.SELECT_MATCH_PREFERENCE);
                    break;
            }

            if (fragment != null) {
                getChildFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.fade_slide_in_right, R.anim.none)
                        .replace(R.id.rating_flow_feedback_content, fragment)
                        .commit();
            }
        }
        else if (getActivity() instanceof RatingFlowActivity) {
            ((RatingFlowActivity) getActivity()).finishStep();
        }
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
}
