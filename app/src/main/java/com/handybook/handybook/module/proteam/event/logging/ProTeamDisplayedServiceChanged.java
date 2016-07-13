package com.handybook.handybook.module.proteam.event.logging;

import com.handybook.handybook.logger.mixpanel.annotation.Track;
import com.handybook.handybook.logger.mixpanel.annotation.TrackField;

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
