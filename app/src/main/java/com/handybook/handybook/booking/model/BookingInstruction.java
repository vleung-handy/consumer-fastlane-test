package com.handybook.handybook.booking.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.R;

import java.util.HashMap;
import java.util.Map;

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


    private static final Map<String, Integer> ICONS;

    static
    {
        ICONS = new HashMap<>();
        ICONS.put("kitchen", R.drawable.ic_instruction_kitchen);
        ICONS.put("bedroom", R.drawable.ic_instruction_bedroom);
        ICONS.put("bathroom", R.drawable.ic_instruction_bathroom);
        ICONS.put("floors", R.drawable.ic_instruction_floor);
        ICONS.put("general", R.drawable.ic_instruction_general);
    }

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

    @DrawableRes
    public final int getImageResource()
    {
        if (ICONS.get(getInstructionType()) != null)
        {
            return ICONS.get(getInstructionType());
        }

        return ICONS.get("general");
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
