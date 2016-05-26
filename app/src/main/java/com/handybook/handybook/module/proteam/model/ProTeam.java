package com.handybook.handybook.module.proteam.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProTeam
{

    @SerializedName(ProTeamCategory.CLEANING)
    private ProTeamCategory mCleaning;
    @SerializedName(ProTeamCategory.HANDYMEN)
    private ProTeamCategory mHandymen;


    public static class ProTeamCategory
    {
        static final String CLEANING = "cleaning";
        static final String HANDYMEN = "handymen";

        @SerializedName(ProviderMatchPreference.Constants.STRING_VALUE_PREFERRED)
        private List<ProTeamPro> mPreferred;
        @SerializedName(ProviderMatchPreference.Constants.STRING_VALUE_INDIFFERENT)
        private List<ProTeamPro> mIndifferent;
        @SerializedName(ProviderMatchPreference.Constants.STRING_VALUE_NEVER)
        private List<ProTeamPro> mNever;

        @Nullable
        public List<ProTeamPro> getPreferred()
        {
            return mPreferred;
        }

        @Nullable
        List<ProTeamPro> getIndifferent()
        {
            return mIndifferent;
        }

        @Nullable
        List<ProTeamPro> getNever()
        {
            return mNever;
        }
    }


    /**
     * Passed to the API to move a group of providers to preferred, indifferent or never buckets
     */
    static class ProTeamEdit
    {
        @SerializedName(ProTeamCategory.CLEANING)
        private List<Integer> mCleaningIds;
        @SerializedName(ProTeamCategory.HANDYMEN)
        private List<Integer> mHandymenIds;
        @SerializedName("match_preference")
        private ProviderMatchPreference mMatchPreference;

        public ProTeamEdit(final ProviderMatchPreference matchPreference)
        {
            mMatchPreference = matchPreference;
        }

        //FIXME: Add accessors memthods as needed while keeping this as closed as possible

    }
}
