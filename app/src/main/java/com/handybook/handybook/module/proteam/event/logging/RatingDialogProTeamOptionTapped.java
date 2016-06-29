package com.handybook.handybook.module.proteam.event.logging;

import com.handybook.handybook.analytics.annotation.Track;

/**
 * Triggers when user taps one of the pro team buttons in the rating flow dialog.
 */
@Track("rating_dialog_pro_team_option_tapped")
public class RatingDialogProTeamOptionTapped extends RatingDialogProTeamOption
{
    public RatingDialogProTeamOptionTapped(final boolean enabled, final OptionType optionType)
    {
        super(enabled, optionType);
    }
}
