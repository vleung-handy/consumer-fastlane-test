package com.handybook.handybook.module.proteam.event.logging;

import com.handybook.handybook.logger.mixpanel.annotation.Track;
import com.handybook.handybook.logger.mixpanel.annotation.TrackField;

@Track("pro_team_add_pro_page_opened")
public class ProTeamAddProPageOpened
{
    @TrackField("number_of_cleaning_pros")
    private final int cleaner;

    @TrackField("number_of_handymen_pros")
    private final int handyMen;

    public ProTeamAddProPageOpened(final int cleaner, final int handyMen)
    {
        this.cleaner = cleaner;
        this.handyMen = handyMen;
    }
}
