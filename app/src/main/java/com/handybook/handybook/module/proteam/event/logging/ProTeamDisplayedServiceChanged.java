package com.handybook.handybook.module.proteam.event.logging;

import com.handybook.handybook.analytics.annotation.Track;
import com.handybook.handybook.analytics.annotation.TrackField;

@Track("pro_team_displayed_service_changed")
public class ProTeamDisplayedServiceChanged
{
    @TrackField("selected_service")
    private final String mSelectedService;

    public ProTeamDisplayedServiceChanged(final String selectedService)
    {
        mSelectedService = selectedService;
    }
}
