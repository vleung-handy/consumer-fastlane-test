package com.handybook.handybook;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public final class Booking implements Parcelable {
    @SerializedName("id") private String id;
    @SerializedName("booking_status") private int isPast;
    @SerializedName("service_name") private String service;
    @SerializedName("date_start") private Date startDate;
    @SerializedName("hours") private float hours;

    final String getId() {
        return id;
    }

    final void setId(final String id) {
        this.id = id;
    }

    final boolean isPast() {
        return isPast == 1;
    }

    final void setIsPast(final boolean isPast) {
        if (isPast) this.isPast = 1;
        else this.isPast = 0;
    }

    final String getService() {
        return service;
    }

    final void setService(final String service) {
        this.service = service;
    }

    final Date getStartDate() {
        return startDate;
    }

    final void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    final float getHours() {
        return hours;
    }

    final void setHours(float hours) {
        this.hours = hours;
    }

    private Booking(final Parcel in) {
        final String[] stringData = new String[2];
        in.readStringArray(stringData);
        id = stringData[0];
        service = stringData[1];

        final int[] intData = new int[1];
        in.readIntArray(intData);
        isPast = intData[0];

        final float[] floatData = new float[1];
        in.readFloatArray(floatData);
        hours = floatData[0];

        startDate = new Date(in.readLong());
    }

    @Override
    public final void writeToParcel(final Parcel out, final int flags) {
        out.writeStringArray(new String[]{ id, service});
        out.writeIntArray(new int[]{ isPast });
        out.writeFloatArray(new float[]{ hours });
        out.writeLong(startDate.getTime());
    }

    @Override
    public final int describeContents(){
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Booking createFromParcel(final Parcel in) {
            return new Booking(in);
        }
        public Booking[] newArray(final int size) {
            return new Booking[size];
        }
    };
}
