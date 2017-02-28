package com.handybook.handybook.booking.bookingedit.model;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.booking.model.BookingInstruction;

import java.util.List;

public final class BookingEditEntryInformationRequest {

    @SerializedName("entry_instructions")
    private List<BookingInstruction> mEntryInstructions;
    @SerializedName("apply_to_all")
    private boolean mApplyToAllInSeries;
    //whether this should be applied to all bookings in the recurring series

    public BookingEditEntryInformationRequest(
            List<BookingInstruction> entryInstructions,
            boolean applyToAllInSeries
    ) {
        mEntryInstructions = entryInstructions;
        mApplyToAllInSeries = applyToAllInSeries;
    }
}
