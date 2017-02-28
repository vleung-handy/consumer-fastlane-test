package com.handybook.handybook.booking.bookingedit.model;

import com.google.gson.annotations.SerializedName;

import java.util.Observable;

//TODO: we will remove the need for making this Observable soon
public final class BookingEditFrequencyRequest extends Observable {

    @SerializedName("new_freq")
    private int mRecurringFrequency;

    public int getRecurringFrequency() {
        return mRecurringFrequency;
    }

    public void setRecurringFrequency(int recurring) {
        this.mRecurringFrequency = recurring;
        triggerObservers();
    }

    //TODO: this function is duplicated in a LOT of other classes - we must consolidate!
    private void triggerObservers() {
        setChanged();
        notifyObservers();
    }
}
