package com.handybook.handybook.booking.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ChecklistItem implements Parcelable
{
    @SerializedName("title")
    private String mTitle;
    @SerializedName("text")
    private String mText;

    protected ChecklistItem(Parcel in)
    {
        mTitle = in.readString();
        mText = in.readString();
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
    public void writeToParcel(final Parcel out, final int flags)
    {
        out.writeString(mTitle);
        out.writeString(mText);
    }
}
