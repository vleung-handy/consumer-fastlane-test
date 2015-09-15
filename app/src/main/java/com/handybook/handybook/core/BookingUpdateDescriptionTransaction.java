package com.handybook.handybook.core;

import com.google.gson.annotations.SerializedName;

import java.util.Observable;

public final class BookingUpdateDescriptionTransaction extends Observable {
    @SerializedName("getin") private String getInId;            //Get-In identifier, numeric, corresponds to value on server  . TODO: Sync values to consts here
    @SerializedName("getintext") private String getInText;     //User entered text that explains additional Get-In information
    @SerializedName("msg_to_pro") private String messageToPro;  //Separate message to pro containing misc information

    public final String getGetInId() {
        return getInId;
    }

    public final void setGetInId(final String getInId) {
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

    public String getMessageToPro()
    {
        return messageToPro;
    }

    public void setMessageToPro(String messageToPro)
    {
        this.messageToPro = messageToPro;
    }

    private void triggerObservers() {
        setChanged();
        notifyObservers();
    }
}
