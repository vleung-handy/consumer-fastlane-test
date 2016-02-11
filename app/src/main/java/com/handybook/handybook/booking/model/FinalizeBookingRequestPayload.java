package com.handybook.handybook.booking.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Body of POST request to /api/v3/bookings/:id/finalize_booking
 */
public class FinalizeBookingRequestPayload
{
    @SerializedName("password")
    private String mPassword;
    @SerializedName("apply_to_all")
    private Boolean mShouldApplyToAll;
    @SerializedName("booking_instructions")
    private ArrayList<BookingInstruction> mBookingInstructions;

    public String getPassword()
    {
        return mPassword;
    }

    public void setPassword(final String password)
    {
        mPassword = password;
    }

    public Boolean getShouldApplyToAll()
    {
        return mShouldApplyToAll;
    }

    public void setShouldApplyToAll(final Boolean shouldApplyToAll)
    {
        mShouldApplyToAll = shouldApplyToAll;
    }

    public ArrayList<BookingInstruction> getBookingInstructions()
    {
        return mBookingInstructions;
    }

    public void setBookingInstructions(final ArrayList<BookingInstruction> bookingInstructions)
    {
        mBookingInstructions = bookingInstructions;
    }

    public static FinalizeBookingRequestPayload fromJson(final String jsonIn)
    {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(jsonIn, FinalizeBookingRequestPayload.class);
    }

    public String toJson()
    {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        return gson.toJson(this);
    }
}
