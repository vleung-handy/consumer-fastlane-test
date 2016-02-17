package com.handybook.handybook.booking.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.R;

import java.util.HashMap;
import java.util.Map;

public class ChecklistItem implements Parcelable
{
    @SerializedName("id")
    private Long mId;
    @SerializedName("machine_name")
    private String mMachineName;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("text")
    private String mText;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("instruction_type")
    private String mInstructionType;
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

    protected ChecklistItem(Parcel in)
    {
        mId = (Long) in.readValue(Long.class.getClassLoader());
        mMachineName = in.readString();
        mTitle = in.readString();
        mText = in.readString();
        mDescription = in.readString();
        mInstructionType = in.readString();
        mIsRequested = (in.readInt() == 0) ? false : true;
    }

    public static final Creator<ChecklistItem> CREATOR = new Creator<ChecklistItem>()
    {
        @Override
        public ChecklistItem createFromParcel(Parcel in)
        {
            return new ChecklistItem(in);
        }

        @Override
        public ChecklistItem[] newArray(int size)
        {
            return new ChecklistItem[size];
        }
    };

    public String getTitle()
    {
        return mTitle;
    }

    public String getText()
    {
        return mText;
    }

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
        dest.writeString(mTitle);
        dest.writeString(mText);
        dest.writeString(mDescription);
        dest.writeString(mInstructionType);
        dest.writeInt((mIsRequested != null && mIsRequested) ? 1 : 0);
    }
}
