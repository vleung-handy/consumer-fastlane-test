package com.handybook.handybook.ratingflow;

import com.handybook.handybook.ratingflow.ui.RatingFlowActivity;
import com.handybook.handybook.ratingflow.ui.RatingFlowFeedbackFragment;
import com.handybook.handybook.ratingflow.ui.RatingFlowHelpDialogFragment;
import com.handybook.handybook.ratingflow.ui.RatingFlowImprovementFragment;
import com.handybook.handybook.ratingflow.ui.RatingFlowMatchPreferenceFragment;
import com.handybook.handybook.ratingflow.ui.RatingFlowRateAndTipFragment;
import com.handybook.handybook.ratingflow.ui.RatingFlowReferralFragment;
import com.handybook.handybook.ratingflow.ui.RatingFlowReviewFragment;
import com.handybook.handybook.ratingflow.ui.RatingFlowShareProFragment;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                RatingFlowActivity.class,
                RatingFlowRateAndTipFragment.class,
                RatingFlowFeedbackFragment.class,
                RatingFlowMatchPreferenceFragment.class,
                RatingFlowReviewFragment.class,
                RatingFlowReferralFragment.class,
                RatingFlowImprovementFragment.class,
                RatingFlowHelpDialogFragment.class,
                RatingFlowShareProFragment.class,
        })
public class RatingFlowModule {
}
