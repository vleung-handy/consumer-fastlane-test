package com.handybook.handybook.booking.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class BookingInstruction implements Parcelable
{
    @SerializedName("id")
    private Long mId;
    @SerializedName("machine_name")
    private String mMachineName;
    @SerializedName("instruction_type")
    private String mInstructionType;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("requested")
    private Boolean mIsRequested;


    public BookingInstruction(
            final Long id,
            final String machineName,
            final String instructionType,
            final String description,
            final Boolean isRequested
    )
    {
        mId = id;
        mMachineName = machineName;
        mInstructionType = instructionType;
        mDescription = description;
        mIsRequested = isRequested;
    }

    protected BookingInstruction(Parcel in)
    {
        mId = (Long) in.readValue(Long.class.getClassLoader());
        mMachineName = in.readString();
        mInstructionType = in.readString();
        mDescription = in.readString();
        mIsRequested = (in.readInt() == 0) ? false : true;
    }

    public static final Creator<BookingInstruction> CREATOR = new Creator<BookingInstruction>()
    {
        @Override
        public BookingInstruction createFromParcel(Parcel in)
        {
            return new BookingInstruction(in);
        }

        @Override
        public BookingInstruction[] newArray(int size)
        {
            return new BookingInstruction[size];
        }
    };

    public Long getId()
    {
        return mId;
    }

    public String getMachineName()
    {
        return mMachineName;
    }

    public String getDescription()
    {
        return mDescription;
    }

    public String getInstructionType()
    {
        return mInstructionType;
    }

    public boolean getIsRequested()
    {
        return mIsRequested != null && mIsRequested;
    }

    public void setId(final Long id)
    {
        mId = id;
    }

    public void setMachineName(final String machineName)
    {
        mMachineName = machineName;
    }

    public void setInstructionType(final String instructionType)
    {
        mInstructionType = instructionType;
    }

    public void setDescription(final String description)
    {
        mDescription = description;
    }

    public void setIsRequested(final Boolean isRequested)
    {
        mIsRequested = isRequested;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags)
    {
        dest.writeValue(mId);
        dest.writeString(mMachineName);
        dest.writeString(mInstructionType);
        dest.writeString(mDescription);
        dest.writeInt((mIsRequested != null && mIsRequested) ? 1 : 0);
    }
}
