package com.handybook.handybook.module.proteam.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Passed to the API to move a group of providers to preferred, indifferent or never buckets
 */
public class ProTeamEdit
{
    @SerializedName(ProTeamCategoryType.Constants.CLEANING)
    private List<Integer> mCleaningIds = null;
    @SerializedName(ProTeamCategoryType.Constants.HANDYMEN)
    private List<Integer> mHandymenIds = null;
    @SerializedName("match_preference")
    private ProviderMatchPreference mMatchPreference;

    public ProTeamEdit(final ProviderMatchPreference matchPreference)
    {
        mMatchPreference = matchPreference;
    }


    public ProviderMatchPreference getMatchPreference()
    {
        return mMatchPreference;
    }

    public void addCleaningId(final int id)
    {
        if (mCleaningIds == null)
        {
            mCleaningIds = new ArrayList<>();
        }
        mCleaningIds.add(id);
    }

    public void addHandymenId(final int id)
    {
        if (mHandymenIds == null)
        {
            mHandymenIds = new ArrayList<>();
        }
        mHandymenIds.add(id);
    }
}
