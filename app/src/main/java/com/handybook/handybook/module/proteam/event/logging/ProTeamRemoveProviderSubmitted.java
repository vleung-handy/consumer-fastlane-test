package com.handybook.handybook.module.proteam.event.logging;

import com.handybook.handybook.logger.mixpanel.annotation.Track;
import com.handybook.handybook.logger.mixpanel.annotation.TrackField;

@Track("pro_team_remove_provider_submitted")
public class ProTeamRemoveProviderSubmitted
{
    @TrackField("provider_id")
    private final int mProId;

    @TrackField("match_preference")
    private final String mMatchPreference;

    public ProTeamRemoveProviderSubmitted(final int proId, final String matchPreference)
    {
        mProId = proId;
        mMatchPreference = matchPreference;
    }
}
