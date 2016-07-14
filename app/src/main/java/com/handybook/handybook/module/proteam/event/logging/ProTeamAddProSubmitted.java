package com.handybook.handybook.module.proteam.event.logging;


import com.handybook.handybook.logger.mixpanel.annotation.Track;
import com.handybook.handybook.logger.mixpanel.annotation.TrackField;

@Track("pro_team_add_pro_submitted")
public class ProTeamAddProSubmitted
{
    @TrackField("number_of_cleaning_pros")
    private final int mCleaner;

    @TrackField("number_of_handymen_pros")
    private final int mHandyMen;

    public ProTeamAddProSubmitted(final int cleaner, final int handyMen)
    {
        mCleaner = cleaner;
        mHandyMen = handyMen;
    }
}
