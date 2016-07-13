package com.handybook.handybook.module.proteam.event.logging;

import com.handybook.handybook.logger.mixpanel.annotation.TrackField;

public class ProTeamAddProDisplayedServiceChanged
{
    @TrackField("selected_service")
    private final String mSelectedService;

    public ProTeamAddProDisplayedServiceChanged(final String selectedService)
    {
        mSelectedService = selectedService;
    }
}
