package com.handybook.handybook.ratingflow.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class RatingFlowFeedbackChildFragment extends InjectedFragment {

    @Bind(R.id.rating_flow_section_title)
    TextView mSectionTitle;
    @Bind(R.id.rating_flow_section_container)
    ViewGroup mSectionContainer;

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {

        final View view = inflater.inflate(
                R.layout.fragment_rating_flow_generic,
                container,
                false
        );
        ButterKnife.bind(this, view);
        return view;
    }

    abstract void onSubmit();

    protected void finishStep() {
        final RatingFlowFeedbackFragment parentFragment = getRatingFlowFeedbackFragment();
        if (parentFragment != null) {
            parentFragment.continueFeedbackFlow();
        }
    }

    protected void setSubmissionEnabled(final boolean enabled) {
        final RatingFlowFeedbackFragment parentFragment = getRatingFlowFeedbackFragment();
        if (parentFragment != null) {
            parentFragment.mNextButton.setEnabled(enabled);
        }
    }

    @Nullable
    private RatingFlowFeedbackFragment getRatingFlowFeedbackFragment() {
        final Fragment parentFragment = getParentFragment();
        if (parentFragment != null && parentFragment instanceof RatingFlowFeedbackFragment) {
            return ((RatingFlowFeedbackFragment) parentFragment);
        }
        else {
            return null;
        }
    }
}
