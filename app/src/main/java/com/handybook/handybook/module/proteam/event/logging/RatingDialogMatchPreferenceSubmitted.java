package com.handybook.handybook.module.proteam.event.logging;

import com.handybook.handybook.analytics.annotation.Track;
import com.handybook.handybook.analytics.annotation.TrackField;

@Track("rating_dialog_match_preference_submitted")
public class RatingDialogMatchPreferenceSubmitted
{
    @TrackField("match_preference")
    private final String mMatchPreference;

    public RatingDialogMatchPreferenceSubmitted(final String matchPreference)
    {
        mMatchPreference = matchPreference;
    }
}
