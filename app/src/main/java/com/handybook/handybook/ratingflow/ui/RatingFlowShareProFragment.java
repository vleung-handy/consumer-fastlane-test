package com.handybook.handybook.ratingflow.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.EventType;
import com.handybook.handybook.ratingflow.RatingFlowLog;
import com.handybook.handybook.referral.model.ProReferral;
import com.handybook.handybook.referral.model.ReferralDescriptor;
import com.handybook.handybook.referral.ui.SimpleProReferralFragment;

import java.util.Arrays;

/**
 * This is the fragment that is used during the rating flow. Displays information so user can
 * "share" her pro.
 */
public class RatingFlowShareProFragment extends RatingFlowFeedbackChildFragment {

    private ProReferral mProReferral;
    private ReferralDescriptor mReferralDescriptor;
    private Provider mProvider;

    public static RatingFlowShareProFragment newInstance(
            @NonNull final ProReferral proReferral,
            @NonNull final ReferralDescriptor referralDescriptor,
            @NonNull final Provider provider
    ) {

        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.PRO_REFERRAL, proReferral);
        args.putSerializable(BundleKeys.REFERRAL_DESCRIPTOR, referralDescriptor);
        args.putSerializable(BundleKeys.PROVIDER, provider);

        RatingFlowShareProFragment fragment = new RatingFlowShareProFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProReferral = (ProReferral) getArguments().getSerializable(BundleKeys.PRO_REFERRAL);
        mReferralDescriptor
                = (ReferralDescriptor) getArguments().getSerializable(BundleKeys.REFERRAL_DESCRIPTOR);
        mProvider = (Provider) getArguments().getSerializable(BundleKeys.PROVIDER);

        bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.ReferralPageLog(
                EventType.EVENT_TYPE_SHOWN,
                Arrays.asList(Integer.parseInt(mProvider.getId()))
        )));
    }

    @Override
    public void onViewCreated(
            final View view, @Nullable final Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        adjustIrrelevantViews();

        SimpleProReferralFragment
                frag = (SimpleProReferralFragment) getChildFragmentManager().findFragmentByTag(
                SimpleProReferralFragment.class.getName());
        if (frag == null) {
            frag = SimpleProReferralFragment.newInstance(
                    mProReferral,
                    mReferralDescriptor,
                    mProvider
            );

            getChildFragmentManager().beginTransaction()
                                     .add(
                                             R.id.rating_flow_section_container,
                                             frag,
                                             SimpleProReferralFragment.class.getName()
                                     )
                                     .commit();
        }
    }

    /**
     * Since this must inherit from {@link RatingFlowFeedbackChildFragment},
     * and that it contains some template views that isn't needed here, those will be
     * toggled View.GONE.
     *
     * For the views that we do need, we need to adjust some properties to make it fit the screen.
     */
    private void adjustIrrelevantViews() {
        mSectionTitle.setVisibility(View.GONE);
        mSectionSubtitle.setVisibility(View.GONE);
        mSectionHelperContainer.setVisibility(View.GONE);

        LinearLayout.LayoutParams newParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        newParams.setMargins(0, 0, 0, 0);
        mSectionContainer.setLayoutParams(newParams);
        mSectionContainer.setPadding(0, 0, 0, 0);
        mSectionContainer.requestLayout();
    }

    @Override
    protected void onSubmit() {

        bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.ReferralPageLog(
                EventType.EVENT_TYPE_SUBMITTED,
                Arrays.asList(Integer.parseInt(mProvider.getId()))
        )));

        finishStep();
    }

    @Override
    void onSkip() {
        bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.ReferralPageLog(
                EventType.EVENT_TYPE_SKIPPED,
                Arrays.asList(Integer.parseInt(mProvider.getId()))
        )));
    }
}
