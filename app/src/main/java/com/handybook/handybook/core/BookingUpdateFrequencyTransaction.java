package com.handybook.handybook.core;

import com.google.gson.annotations.SerializedName;

import java.util.Observable;

public final class BookingUpdateFrequencyTransaction extends Observable {

    @SerializedName("new_freq") private int recurringFrequency;
    public int getRecurringFrequency()
    {
        return recurringFrequency;
    }

    public void setRecurringFrequency(int recurring)
    {
        this.recurringFrequency = recurring;
    }

    private void triggerObservers() {
        setChanged();
        notifyObservers();
    }
}
