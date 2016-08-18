package com.handybook.handybook.module.proteam.model;


import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.util.DateTimeUtils;


public class ProTeamWrapper
{

    @SerializedName("pro_team")
    private ProTeam mProTeam;
    @SerializedName("pro_team_help_center_url")
    private String mProTeamHelpCenterUrl;

    @Nullable
    public ProTeam getProTeam()
    {
        return mProTeam;
    }

    public String getProTeamHelpCenterUrl()
    {
        return mProTeamHelpCenterUrl;
    }

    @Nullable
    public static ProTeamWrapper fromJson(@Nullable final String json)
    {
        if (json == null) { return null; }

        return new GsonBuilder().setDateFormat(DateTimeUtils.UNIVERSAL_DATE_FORMAT).create()
                .fromJson(json, ProTeamWrapper.class);
    }
}
