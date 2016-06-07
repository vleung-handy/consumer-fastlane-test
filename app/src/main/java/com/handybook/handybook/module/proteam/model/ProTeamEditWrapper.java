package com.handybook.handybook.module.proteam.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.util.DateTimeUtils;

public class ProTeamEditWrapper
{
    @SerializedName("pro_team")
    private ProTeamEdit mProTeamEdit;

    @Nullable
    public ProTeamEdit getProTeamEdit()
    {
        return mProTeamEdit;
    }

    @Nullable
    public static ProTeamEditWrapper fromJson(@NonNull final String json)
    {
        return new GsonBuilder().setDateFormat(DateTimeUtils.UNIVERSAL_DATE_FORMAT).create()
                .fromJson(json, ProTeamEditWrapper.class);
    }
}
