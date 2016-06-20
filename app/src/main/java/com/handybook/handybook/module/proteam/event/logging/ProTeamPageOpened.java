package com.handybook.handybook.module.proteam.event.logging;

import com.handybook.handybook.analytics.annotation.Track;
import com.handybook.handybook.analytics.annotation.TrackField;

/**
 * I want to be able to filter out views based on if they were able to add a pro or
 * not and how many are on the list
 */
@Track("pro_team_page_opened")
public class ProTeamPageOpened
{

    @TrackField("number_of_cleaning_pros")
    private final int mCleaner;

    @TrackField("number_of_handymen_pros")
    private final int mHandyMen;

    public ProTeamPageOpened(final int cleaner, final int handyMen)
    {
        mCleaner = cleaner;
        mHandyMen = handyMen;
    }
}
