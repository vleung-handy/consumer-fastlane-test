package com.handybook.handybook.module.proteam.event.logging;

import com.handybook.handybook.analytics.annotation.Track;
import com.handybook.handybook.analytics.annotation.TrackField;

/**
 * triggers when user submits rating (with pro team option and tip if applicable)
 */
@Track("rating_dialog_submitted")
public class RatingDialogSubmitted
{
    @TrackField("rating")
    private final int mRating;

    @TrackField("match_preference")
    private final String mMatchPreferences;

    @TrackField("tip_amount")
    private final Integer mTipAmount;

    public RatingDialogSubmitted(final int rating, final String matchPreferences, final Integer tipAmount)
    {
        mRating = rating;
        mMatchPreferences = matchPreferences;
        mTipAmount = tipAmount;
    }
}
