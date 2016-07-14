package com.handybook.handybook.module.proteam.event.logging;

import com.handybook.handybook.logger.mixpanel.annotation.Track;

/**
 * Triggered when pro team option is presented in the rating dialog
 */
@Track("rating_dialog_pro_team_option_presented")
public class RatingDialogProTeamOptionPresented extends RatingDialogProTeamOption
{
    public RatingDialogProTeamOptionPresented(
            final boolean enabled,
            final OptionType optionType
    )
    {
        super(enabled, optionType);
    }

}
