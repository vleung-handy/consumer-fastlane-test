package com.handybook.handybook.proteam.model;

import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.library.util.DateTimeUtils;

import java.io.Serializable;
import java.util.List;

public class BookingProTeam implements Serializable
{
    @SerializedName("pro_team")
    private List<ProTeamPro> mProTeamPros;

    public List<ProTeamPro> getProTeamPros()
    {
        return mProTeamPros;
    }

    @Nullable
    public static BookingProTeam fromJson(@Nullable final String json)
    {
        if (json == null) { return null; }

        return new GsonBuilder().setDateFormat(DateTimeUtils.UNIVERSAL_DATE_FORMAT).create()
                                .fromJson(json, BookingProTeam.class);
    }
}
