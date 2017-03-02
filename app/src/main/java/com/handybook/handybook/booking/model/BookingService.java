package com.handybook.handybook.booking.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a service object within the context of a booking. Note that this is a
 * different object than {@code Service}
 */
public class BookingService implements Parcelable {

    @SerializedName("id")
    private int mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("machine_name")
    protected String mMachineName;

    @NonNull
    @SerializedName("parent")
    protected BookingService mParent;

    private BookingService(final Parcel in) {
        final String[] stringData = new String[2];
        in.readStringArray(stringData);
        mName = stringData[0];
        mMachineName = stringData[1];

        final int[] intData = new int[1];
        in.readIntArray(intData);
        mId = intData[0];

        mParent = in.readParcelable(BookingService.class.getClassLoader());
    }

    public String getMachineName() {
        return mMachineName;
    }

    @NonNull
    public BookingService getParent() {
        return mParent;
    }

    @Override
    public final void writeToParcel(final Parcel out, final int flags) {
        out.writeStringArray(new String[]{mName, mMachineName});
        out.writeIntArray(new int[]{mId});
        out.writeParcelable(mParent, 0);
    }

    @Override
    public final int describeContents() {
        return 0;
    }

    public static final Creator CREATOR = new Creator() {
        public BookingService createFromParcel(final Parcel in) {
            return new BookingService(in);
        }

        public BookingService[] newArray(final int size) {
            return new BookingService[size];
        }
    };

}
