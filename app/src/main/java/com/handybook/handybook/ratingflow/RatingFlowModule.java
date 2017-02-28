package com.handybook.handybook.ratingflow;

import com.handybook.handybook.ratingflow.ui.RatingFlowActivity;
import com.handybook.handybook.ratingflow.ui.RatingFlowFeedbackFragment;
import com.handybook.handybook.ratingflow.ui.RatingFlowMatchPreferenceFragment;
import com.handybook.handybook.ratingflow.ui.RatingFlowRateAndTipFragment;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                RatingFlowActivity.class,
                RatingFlowRateAndTipFragment.class,
                RatingFlowFeedbackFragment.class,
                RatingFlowMatchPreferenceFragment.class,
        })
public class RatingFlowModule {
}
