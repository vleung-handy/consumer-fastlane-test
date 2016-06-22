package com.handybook.handybook.module.proteam.event.logging;

import com.handybook.handybook.analytics.annotation.Track;
import com.handybook.handybook.analytics.annotation.TrackField;

@Track("rating_dialog_match_preference_changed")
public class RatingDialogMatchPreferenceChanged
{
    @TrackField("match_preference")
    private final String mMatchPreference;

    public RatingDialogMatchPreferenceChanged(final String matchPreference)
    {
        mMatchPreference = matchPreference;
    }
}
