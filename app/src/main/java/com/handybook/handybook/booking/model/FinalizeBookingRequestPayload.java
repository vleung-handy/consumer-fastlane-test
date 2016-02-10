package com.handybook.handybook.booking.model;

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
}
