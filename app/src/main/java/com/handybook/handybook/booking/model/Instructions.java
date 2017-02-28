package com.handybook.handybook.booking.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Instructions implements Parcelable {

    @SerializedName("title")
    private String mTitle;
    @SerializedName("instructions_checklist")
    private List<BookingInstruction> mBookingInstructions;

    protected Instructions(Parcel in) {
        mTitle = in.readString();
        mBookingInstructions = new ArrayList<>();
        in.readTypedList(mBookingInstructions, BookingInstruction.CREATOR);
    }

    public static final Creator<Instructions> CREATOR = new Creator<Instructions>() {
        @Override
        public Instructions createFromParcel(Parcel in) {
            return new Instructions(in);
        }

        @Override
        public Instructions[] newArray(int size) {
            return new Instructions[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public List<BookingInstruction> getBookingInstructions() {
        return mBookingInstructions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel out, final int flags) {
        out.writeString(mTitle);
        out.writeTypedList(mBookingInstructions);
    }
}
