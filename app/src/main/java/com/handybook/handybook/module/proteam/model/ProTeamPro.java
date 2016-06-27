package com.handybook.handybook.module.proteam.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Object describing one provider in a ProTeam
 */
public class ProTeamPro implements Parcelable
{
    private static final int MILLIS_FOR_NULL_DATE = -1;

    @SerializedName("id")
    private int mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("last_seen_at")
    private Date mLastSeenAt;

    protected ProTeamPro(Parcel in)
    {
        mId = in.readInt();
        mName = in.readString();
        mDescription = in.readString();
        final long millis = in.readLong();
        if (millis == MILLIS_FOR_NULL_DATE) // null date was stored
        {
            mLastSeenAt = null; //Explicit is better than implicit
        }
        else
        {
            mLastSeenAt = new Date(millis);
        }
    }

    public static final Creator<ProTeamPro> CREATOR = new Creator<ProTeamPro>()
    {
        @Override
        public ProTeamPro createFromParcel(Parcel in)
        {
            return new ProTeamPro(in);
        }

        @Override
        public ProTeamPro[] newArray(int size)
        {
            return new ProTeamPro[size];
        }
    };

    public int getId()
    {
        return mId;
    }

    @Nullable
    public String getName()
    {
        return mName;
    }

    @Nullable
    public String getDescription()
    {
        return mDescription;
    }

    @Nullable
    public Date getLastSeenAt()
    {
        return mLastSeenAt;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags)
    {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeString(mDescription);
        dest.writeLong(mLastSeenAt != null ? mLastSeenAt.getTime() : MILLIS_FOR_NULL_DATE);
    }
}
