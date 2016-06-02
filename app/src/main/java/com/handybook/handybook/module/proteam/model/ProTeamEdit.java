package com.handybook.handybook.module.proteam.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Passed to the API to move a group of providers to preferred, indifferent or never buckets
 */
public class ProTeamEdit
{
    @SerializedName(ProTeamCategoryType.Constants.CLEANING)
    private List<Integer> mCleaningIds;
    @SerializedName(ProTeamCategoryType.Constants.HANDYMEN)
    private List<Integer> mHandymenIds;
    @SerializedName("match_preference")
    private ProviderMatchPreference mMatchPreference;

    public ProTeamEdit(final ProviderMatchPreference matchPreference)
    {
        mMatchPreference = matchPreference;
    }

    //FIXME: Add accessors memthods as needed while keeping this as closed as possible

}
