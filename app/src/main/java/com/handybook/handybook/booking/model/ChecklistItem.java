package com.handybook.handybook.booking.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ChecklistItem implements Parcelable
{
    @SerializedName("id")
    private Long mId;
    @SerializedName("mahine_name")
    private String mMachineName;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("text")
    private String mText;
    @SerializedName("state")
    private String mState;


    protected ChecklistItem(Parcel in)
    {
        mId = (Long) in.readValue(Long.class.getClassLoader());
        mMachineName = in.readString();
        mTitle = in.readString();
        mText = in.readString();
        mState = in.readString();
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
        dest.writeString(mState);
    }
}
