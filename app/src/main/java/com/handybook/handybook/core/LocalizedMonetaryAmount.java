package com.handybook.handybook.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class LocalizedMonetaryAmount implements Parcelable {
    @SerializedName("amount_in_cents")
    private int amountInCents;
    @SerializedName("display_amount")
    private String displayAmount;

    public int getAmountInCents() {
        return amountInCents;
    }

    public String getDisplayAmount() {
        return displayAmount;
    }

    private LocalizedMonetaryAmount(final Parcel in) {
        final String[] stringData = new String[1];
        in.readStringArray(stringData);
        displayAmount = stringData[0];

        final int[] intData = new int[1];
        in.readIntArray(intData);
        amountInCents = intData[0];
    }

    @Override
    public final int describeContents() {
        return 0;
    }

    @Override
    public final void writeToParcel(final Parcel out, final int flags) {
        out.writeIntArray(new int[]{amountInCents});
        out.writeStringArray(new String[]{displayAmount});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public LocalizedMonetaryAmount createFromParcel(final Parcel in) {
            return new LocalizedMonetaryAmount(in);
        }

        public LocalizedMonetaryAmount[] newArray(final int size) {
            return new LocalizedMonetaryAmount[size];
        }
    };

}
