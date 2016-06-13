package com.handybook.handybook.module.proteam.event.logging;

import com.handybook.handybook.analytics.annotation.Track;
import com.handybook.handybook.analytics.annotation.TrackField;

/**
 * This is when the user clicks the "X" button to remove a pro (which triggers a confirm dialog)
 */
@Track("pro_team_remove_provider_tapped")
public class ProTeamRemoveProviderTapped
{
    @TrackField("provider_id")
    private final int mProId;

    public ProTeamRemoveProviderTapped(final int proId)
    {
        mProId = proId;
    }
}
