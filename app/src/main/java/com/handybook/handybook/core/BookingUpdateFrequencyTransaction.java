package com.handybook.handybook.core;

import com.google.gson.annotations.SerializedName;

import java.util.Observable;

public final class BookingUpdateFrequencyTransaction extends Observable
{
    @SerializedName("new_freq")
    private int mRecurringFrequency;

    public int getmRecurringFrequency()
    {
        return mRecurringFrequency;
    }

    public void setRecurringFrequency(int recurring)
    {
        this.mRecurringFrequency = recurring;
        triggerObservers();
    }

    //TODO: this function is duplicated in a LOT of other classes - we must consolidate!
    private void triggerObservers()
    {
        setChanged();
        notifyObservers();
    }
}
