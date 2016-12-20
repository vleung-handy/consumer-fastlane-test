package com.handybook.handybook.logger.handylogger.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;

public abstract class RatingDialogLog extends EventLog
{
    private static final String EVENT_CONTEXT = "rating_dialog";

    public RatingDialogLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    /**
     * rating is submitted
     */
    public static class Submitted extends RatingDialogLog
    {
        private static final String EVENT_TYPE = "submitted";

        @SerializedName("rating")
        private final Integer mRating;

        /**
         * this represents the match preference for the provider
         * prior to any remove changes
         */
        @SerializedName("match_preference")
        private final String mProviderMatchPreference;

        @SerializedName("tip_amount")
        private final Integer mTipAmountCents;

        public Submitted(Integer rating,
                         ProviderMatchPreference providerMatchPreference,
                         Integer tipAmountCents)
        {
            super(EVENT_TYPE);
            mRating = rating;
            mProviderMatchPreference = ProviderMatchPreference.asString(providerMatchPreference);
            mTipAmountCents = tipAmountCents;
        }
    }

    public static abstract class ProTeam extends RatingDialogLog
    {
        @SerializedName("enabled")
        private final boolean mOptionEnabled;

        @SerializedName("pro_team_option_type")
        private final String mOptionType;


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

        protected ProTeam(
                @NonNull String eventType,
                boolean optionEnabled,
                OptionType optionType)
        {
            super(eventType);
            mOptionEnabled = optionEnabled;
            mOptionType = optionType == null ? null : optionType.toString();
        }

        /**
         * a pro team option is presented
         */
        public static class OptionPresented extends ProTeam
        {
            private static final String EVENT_TYPE = "pro_team_option_presented";

            public OptionPresented(final boolean optionEnabled,
                                   ProTeam.OptionType optionType)
            {
                super(EVENT_TYPE, optionEnabled, optionType);
            }
        }


        /**
         * a pro team option is tapped
         */
        public static class OptionTapped extends ProTeam
        {
            private static final String EVENT_TYPE = "pro_team_option_tapped";

            public OptionTapped(final boolean optionEnabled,
                                ProTeam.OptionType optionType)
            {
                super(EVENT_TYPE, optionEnabled, optionType);
            }
        }
    }
}
