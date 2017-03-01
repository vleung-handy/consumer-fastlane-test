package com.handybook.handybook.ratingflow.ui;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.handybook.handybook.library.ui.fragment.InjectedFragment;

public abstract class RatingFlowFeedbackChildFragment extends InjectedFragment {

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
