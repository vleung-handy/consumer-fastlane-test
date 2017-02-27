package com.handybook.handybook.booking.bookingedit.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.booking.model.Instructions;

import java.util.Observable;

public final class BookingUpdateNoteToProTransaction extends Observable {

    @SerializedName("msg_to_pro")
    private String mMessageToPro;  //Separate message to pro containing misc information
    @SerializedName("instructions")
    private Instructions mCustomerPreferences;  //Instructions for the pro

    public String getMessageToPro() {
        return mMessageToPro;
    }

    public void setMessageToPro(String messageToPro) {
        mMessageToPro = messageToPro;
        triggerObservers();
    }

    private void triggerObservers() {
        setChanged();
        notifyObservers();
    }

    public void setInstructions(final Instructions customerPreferences) {
        mCustomerPreferences = customerPreferences;
        setChanged();
        notifyObservers();
    }

    @Nullable
    public Instructions getInstructions() {
        return mCustomerPreferences;
    }
}
