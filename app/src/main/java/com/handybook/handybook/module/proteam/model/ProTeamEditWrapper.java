package com.handybook.handybook.module.proteam.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class ProTeamEditWrapper
{
    @SerializedName("pro_team")
    private ProTeam.ProTeamEdit mProTeamEdit;

    @Nullable
    public ProTeam.ProTeamEdit getProTeam()
    {
        return mProTeamEdit;
    }
}
