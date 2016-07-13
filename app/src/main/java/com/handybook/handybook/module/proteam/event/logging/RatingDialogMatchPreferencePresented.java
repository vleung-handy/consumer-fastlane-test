package com.handybook.handybook.module.proteam.event.logging;

import com.handybook.handybook.logger.mixpanel.annotation.Track;
import com.handybook.handybook.logger.mixpanel.annotation.TrackField;

@Track("rating_dialog_match_preference_presented")
public class RatingDialogMatchPreferencePresented
{
    @TrackField("match_preference")
    private final String mMatchPreference;

    public RatingDialogMatchPreferencePresented(final String matchPreference)
    {
        mMatchPreference = matchPreference;
    }
}
