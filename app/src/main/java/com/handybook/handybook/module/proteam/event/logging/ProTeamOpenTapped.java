package com.handybook.handybook.module.proteam.event.logging;


import com.handybook.handybook.logger.mixpanel.annotation.Track;
import com.handybook.handybook.logger.mixpanel.annotation.TrackField;

@Track("pro_team_open_tapped")
public class ProTeamOpenTapped
{

    @TrackField("source_page")
    private final String mSourcePage;

    public ProTeamOpenTapped(final String sourcePage)
    {
        mSourcePage = sourcePage;
    }
}
