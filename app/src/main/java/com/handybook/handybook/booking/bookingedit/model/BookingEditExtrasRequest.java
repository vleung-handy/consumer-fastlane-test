package com.handybook.handybook.booking.bookingedit.model;

import com.google.gson.annotations.SerializedName;

public final class BookingEditExtrasRequest {

    @SerializedName("added_extras")
    private String[] mAddedExtras;

    @SerializedName("removed_extras")
    private String[] mRemovedExtras;

    public void setAddedExtras(String[] addedExtras) {
        mAddedExtras = addedExtras;
    }

    public void setRemovedExtras(String[] removedExtras) {
        mRemovedExtras = removedExtras;
    }
}
