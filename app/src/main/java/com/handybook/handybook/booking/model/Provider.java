package com.handybook.handybook.booking.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public final class Provider implements Parcelable
{
    @SerializedName("status")
    private int status;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("phone")
    private String phone;

    public final int getStatus()
    {
        return status;
    }

    final void setStatus(final int status)
    {
        this.status = status;
    }

    public final String getFirstName()
    {
        return firstName;
    }

    final void setFirstName(final String firstName)
    {
        this.firstName = firstName;
    }

    public final String getLastName()
    {
        return lastName;
    }

    final void setLastName(final String lastName)
    {
        this.lastName = lastName;
    }

    public final String getPhone()
    {
        return phone;
    }

    final void setPhone(final String phone)
    {
        this.phone = phone;
    }

    public final String getFullName()
    {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    private Provider(final Parcel in)
    {
        final int[] intData = new int[1];
        in.readIntArray(intData);
        status = intData[0];

        final String[] stringData = new String[3];
        in.readStringArray(stringData);
        firstName = stringData[0];
        lastName = stringData[1];
        phone = stringData[2];
    }

    @Override
    public final void writeToParcel(final Parcel out, final int flags)
    {
        out.writeIntArray(new int[]{status});
        out.writeStringArray(new String[]{firstName, lastName, phone});
    }

    @Override
    public final int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public Provider createFromParcel(final Parcel in)
        {
            return new Provider(in);
        }

        public Provider[] newArray(final int size)
        {
            return new Provider[size];
        }
    };

    public static final int PROVIDER_STATUS_ASSIGNED = 3; //TODO: Not sure what this is, just conjecturing based on code

}
