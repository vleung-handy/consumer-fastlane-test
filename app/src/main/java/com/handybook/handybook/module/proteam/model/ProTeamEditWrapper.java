package com.handybook.handybook.module.proteam.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class ProTeamEditWrapper
{
    @SerializedName("pro_team_edits")
    private List<ProTeamEdit> mProTeamEdits = new ArrayList<>();
    @SerializedName("source")
    private String mSource;

    public ProTeamEditWrapper(final List<ProTeamEdit> proTeamEdits, final String source)
    {
        mProTeamEdits = proTeamEdits;
        mSource = source;
    }

    @Nullable
    public static ProTeamEditWrapper fromJson(@NonNull final String json)
    {
        return new GsonBuilder().setDateFormat(DateTimeUtils.UNIVERSAL_DATE_FORMAT).create()
                .fromJson(json, ProTeamEditWrapper.class);
    }
}
