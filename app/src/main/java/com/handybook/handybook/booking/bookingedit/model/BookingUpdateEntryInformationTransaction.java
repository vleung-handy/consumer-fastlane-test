package com.handybook.handybook.booking.bookingedit.model;

import com.google.gson.annotations.SerializedName;

import java.util.Observable;

public final class BookingUpdateEntryInformationTransaction extends Observable {
    @SerializedName("getin") private int getInId;            //Get-In identifier, numeric, corresponds to value on server  . TODO: Sync values to consts here
    @SerializedName("getintxt") private String getInText;     //User entered text that explains additional Get-In information

    public final int getGetInId() {
        return getInId;
    }

    public final void setGetInId(final int getInId) {
        this.getInId = getInId;
        triggerObservers();
    }

    final String getGetInText() {
        return getInText;
    }

    public final void setGetInText(final String getInText) {
        this.getInText = getInText;
        triggerObservers();
    }

    private void triggerObservers() {
        setChanged();
        notifyObservers();
    }
}
