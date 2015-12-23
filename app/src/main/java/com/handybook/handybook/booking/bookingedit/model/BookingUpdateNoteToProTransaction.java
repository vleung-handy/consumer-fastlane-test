package com.handybook.handybook.booking.bookingedit.model;

import com.google.gson.annotations.SerializedName;

import java.util.Observable;

public final class BookingUpdateNoteToProTransaction extends Observable
{
    @SerializedName("msg_to_pro")
    private String messageToPro;  //Separate message to pro containing misc information

    public String getMessageToPro()
    {
        return messageToPro;
    }

    public void setMessageToPro(String messageToPro)
    {
        this.messageToPro = messageToPro;
        triggerObservers();
    }

    private void triggerObservers()
    {
        setChanged();
        notifyObservers();
    }
}
