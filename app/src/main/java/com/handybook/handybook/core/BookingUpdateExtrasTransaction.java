package com.handybook.handybook.core;

import com.google.gson.annotations.SerializedName;

public final class BookingUpdateExtrasTransaction
{
    @SerializedName("added_extras")
    private String[] mAddedExtras;

    @SerializedName("removed_extras")
    private String[] mRemovedExtras;

    public void setAddedExtras(String[] addedExtras)
    {
        this.mAddedExtras = addedExtras;
    }

    public void setRemovedExtras(String[] removedExtras)
    {
        this.mRemovedExtras = removedExtras;
    }
}
