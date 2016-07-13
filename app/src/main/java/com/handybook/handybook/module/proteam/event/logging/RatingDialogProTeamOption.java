package com.handybook.handybook.module.proteam.event.logging;

import com.handybook.handybook.analytics.annotation.TrackField;

/**
 * Created by jtse on 6/29/16.
 */
public class RatingDialogProTeamOption
{

    @TrackField("enabled")
    private final boolean mEnabled;

    @TrackField("pro_team_option_type")
    private final String mOptionType;

    public RatingDialogProTeamOption(final boolean enabled, final OptionType optionType)
    {
        mEnabled = enabled;
        mOptionType = optionType.toString();
    }

    public enum OptionType
    {
        ADD("add"),
        REMOVE("remove"),
        BLOCK("block");

        private final String mStringValue;

        OptionType(String stringValue)
        {
            mStringValue = stringValue;
        }

        @Override
        public String toString()
        {
            return mStringValue;
        }
    }

}
