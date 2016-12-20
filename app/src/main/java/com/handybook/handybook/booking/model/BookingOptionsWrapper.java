package com.handybook.handybook.booking.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.model.ProTeamWrapper;

import java.util.List;

public class BookingOptionsWrapper
{
    @SerializedName("booking_options")
    private List<BookingOption> mBookingOptions;
    @SerializedName("provider_preferences")
    private ProTeamWrapper mProTeamWrapper;

    public BookingOptionsWrapper() {}

    public List<BookingOption> getBookingOptions()
    {
        return mBookingOptions;
    }

    @Nullable
    public ProTeam getProTeam()
    {
        return mProTeamWrapper == null ? null : mProTeamWrapper.getProTeam();
    }
}
