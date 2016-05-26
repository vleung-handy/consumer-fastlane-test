package com.handybook.handybook.module.proteam.model;


import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class ProTeamWrapper
{

    @SerializedName("pro_team")
    private ProTeam mProTeam;

    @Nullable
    public ProTeam getProTeam()
    {
        return mProTeam;
    }
}
