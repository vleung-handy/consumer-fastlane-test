package com.handybook.handybook.logger.handylogger.model;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.constants.SourcePage.HandyLoggerSourcePage;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class ProTeamPageLog extends EventLog
{
    private static final String EVENT_CONTEXT = "pro_team_management";

    public static class Context
    {
        /*
        making this part of the pro team log class
        because corresponding server key name for this is
        "provider_team_context"
         */
        public static final String MAIN_MANAGEMENT = "main_management";
    }
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            Context.MAIN_MANAGEMENT,
    })
    public @interface ProviderTeamContext
    {

    }
    protected ProTeamPageLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    /**
     * user taps to open pro team management
     */
    public static class OpenTapped extends ProTeamPageLog
    {
        private static final String EVENT_TYPE = "open_tapped";

        @SerializedName("source_page")
        private final String mSourcePage;

        public OpenTapped(@HandyLoggerSourcePage String sourcePage)
        {
            super(EVENT_TYPE);
            mSourcePage = sourcePage;
        }
    }

    /**
     * pro team management page opened
     */
    public static class PageOpened extends ProTeamPageLog
    {
        private static final String EVENT_TYPE = "page_opened";

        @SerializedName("preferred_cleaning_provider_count")
        private final int mPreferredCleaningProviderCount;

        @SerializedName("indifferent_cleaning_provider_count")
        private final int mIndifferentCleaningProviderCount;

        @SerializedName("preferred_handymen_provider_count")
        private final int mPreferredHandymenProviderCount;

        @SerializedName("indifferent_handymen_provider_count")
        private final int mIndifferentHandymenProviderCount;

        public PageOpened(
                int preferredCleaningProviderCount,
                int indifferentCleaningProviderCount,
                int preferredHandymenProviderCount,
                int indifferentHandymenProviderCount
        )
        {
            super(EVENT_TYPE);
            mPreferredCleaningProviderCount = preferredCleaningProviderCount;
            mIndifferentCleaningProviderCount = indifferentCleaningProviderCount;
            mPreferredHandymenProviderCount = preferredHandymenProviderCount;
            mIndifferentHandymenProviderCount = indifferentHandymenProviderCount;
        }
    }


    /**
     * the checkbox (heart) next to the pro list entry is tapped
     */
    public static class EnableButtonTapped extends ProTeamPageLog
    {
        private static final String EVENT_TYPE = "enable_button_tapped";

        @SerializedName("provider_id")
        private final String mProviderId;

        @SerializedName("preferred")
        private final boolean mPreferredStatusSelected;

        @SerializedName("provider_team_context")
        private final String mProviderTeamContext;

        public EnableButtonTapped(String providerId,
                                  boolean preferredStatusSelected,
                                  @ProviderTeamContext String providerTeamContext)
        {
            super(EVENT_TYPE);
            mProviderId = providerId;
            mPreferredStatusSelected = preferredStatusSelected;
            mProviderTeamContext = providerTeamContext;

        }
    }


    /**
     * a request to update the pro team is submitted
     */
    public static class UpdateSubmitted extends ProTeamPageLog
    {
        private static final String EVENT_TYPE = "update_submitted";

        @SerializedName("added_count")
        private final int mAddedCount;

        @SerializedName("removed_count")
        private final int mRemovedCount;

        @SerializedName("provider_team_context")
        private final String mProviderTeamContext;

        public UpdateSubmitted(
                int addedCount, int removedCount, @ProviderTeamContext String providerTeamContext)
        {
            super(EVENT_TYPE);
            mAddedCount = addedCount;
            mRemovedCount = removedCount;
            mProviderTeamContext = providerTeamContext;

        }
    }


    /**
     * a request to update the pro team is submitted
     */
    public static class FacebookConnectTapped extends ProTeamPageLog
    {
        private static final String EVENT_TYPE = "facebook_connect_tapped";

        @SerializedName("num_preferred_pros")
        private final int mNumPreferredPros;

        public FacebookConnectTapped(int numPreferredPros)
        {
            super(EVENT_TYPE);
            mNumPreferredPros = numPreferredPros;
        }
    }

    public abstract static class BlockProvider extends ProTeamPageLog
    {
        @SerializedName("provider_id")
        private final String mProviderId;

        /**
         * this represents the match preference for the provider
         * prior to any remove changes
         */
        @SerializedName("match_preference")
        private final String mPriorMatchPreference;

        @SerializedName("provider_team_context")
        private final String mProviderTeamContext;

        public BlockProvider(
                String eventType,
                String providerId,
                ProviderMatchPreference providerMatchPreference,
                @ProviderTeamContext String providerTeamContext
        )
        {
            super(eventType);
            mProviderId = providerId;
            mPriorMatchPreference = ProviderMatchPreference.asString(providerMatchPreference);
            mProviderTeamContext = providerTeamContext;

        }

        /**
         * the block provider button (x icon) is tapped
         */
        public static class Tapped extends BlockProvider
        {
            private static final String EVENT_TYPE = "block_provider_tapped";
            public Tapped(final String providerId,
                          final ProviderMatchPreference providerMatchPreference,
                          @ProviderTeamContext final String providerTeamContext)
            {
                super(EVENT_TYPE, providerId, providerMatchPreference, providerTeamContext);
            }
        }


        /**
         * the "block" option in the warning/options dialog is pressed
         * so the block option is submitted
         */
        public static class Submitted extends BlockProvider
        {
            private static final String EVENT_TYPE = "block_provider_submitted";
            public Submitted(final String providerId,
                             final ProviderMatchPreference providerMatchPreference,
                             @ProviderTeamContext final String providerTeamContext)
            {
                super(EVENT_TYPE, providerId, providerMatchPreference, providerTeamContext);
            }
        }
    }

}
