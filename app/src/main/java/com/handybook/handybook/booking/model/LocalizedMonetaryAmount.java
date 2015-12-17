package com.handybook.handybook.booking.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class LocalizedMonetaryAmount implements Parcelable
{
    @SerializedName("amount_in_cents")
    private int mAmountInCents;
    @SerializedName("display_amount")
    private String mDisplayAmount;

    public int getAmountInCents()
    {
        return mAmountInCents;
    }

    public String getDisplayAmount()
    {
        return mDisplayAmount;
    }

    private LocalizedMonetaryAmount(final Parcel in)
    {
        final int[] intData = new int[1];
        in.readIntArray(intData);
        mAmountInCents = intData[0];

        final String[] stringData = new String[1];
        in.readStringArray(stringData);
        mDisplayAmount = stringData[0];
    }

    @Override
    public final int describeContents()
    {
        return 0;
    }

    @Override
    public final void writeToParcel(final Parcel out, final int flags)
    {
        out.writeIntArray(new int[]{mAmountInCents});
        out.writeStringArray(new String[]{mDisplayAmount});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public LocalizedMonetaryAmount createFromParcel(final Parcel in)
        {
            return new LocalizedMonetaryAmount(in);
        }

        public LocalizedMonetaryAmount[] newArray(final int size)
        {
            return new LocalizedMonetaryAmount[size];
        }
    };

}
