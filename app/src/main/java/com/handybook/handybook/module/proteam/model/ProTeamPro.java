package com.handybook.handybook.module.proteam.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Object describing one provider in a ProTeam
 */
public class ProTeamPro implements Parcelable, Comparable
{
    private static final int MILLIS_FOR_NULL_DATE = -1;
    private static final float INVALID_AVERAGE_RATING = -1;

    @SerializedName("id")
    private int mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("team_type")
    private ProTeamCategoryType mCategoryType;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("last_seen_at")
    private Date mLastSeenAt;
    @SerializedName("average_rating")
    private Float mAverageRating;
    @SerializedName("booking_count")
    private Integer mBookingCount;
    @SerializedName("profile_photo_url")
    private String mImageUrl;

    protected ProTeamPro(Parcel in)
    {
        mId = in.readInt();
        mName = in.readString();
        mCategoryType = (ProTeamCategoryType) in.readSerializable();
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
        mAverageRating = in.readFloat();
        if (mAverageRating == INVALID_AVERAGE_RATING)
        {
            mAverageRating = null;
        }
        mBookingCount = in.readInt();
        mImageUrl = in.readString();
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

    public ProTeamCategoryType getCategoryType()
    {
        return mCategoryType;
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

    @Nullable
    public Float getAverageRating()
    {
        return mAverageRating;
    }

    @Nullable
    public Integer getBookingCount()
    {
        return mBookingCount;
    }

    public String getImageUrl()
    {
        return mImageUrl;
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
        dest.writeSerializable(mCategoryType);
        dest.writeString(mDescription);
        dest.writeLong(mLastSeenAt != null ? mLastSeenAt.getTime() : MILLIS_FOR_NULL_DATE);
        dest.writeFloat(mAverageRating == null ? INVALID_AVERAGE_RATING : mAverageRating);
        dest.writeInt(mBookingCount);
        dest.writeString(mImageUrl);
    }

    @Override
    public String toString()
    {
        return "[" + mName + "]";
    }

    @Override
    public int compareTo(final Object object)
    {
        if (object instanceof ProTeamPro)
        {
            final ProTeamPro otherProTeamPro = (ProTeamPro) object;
            if (mLastSeenAt == null && otherProTeamPro.mLastSeenAt == null) { return 0; }
            if (otherProTeamPro.mLastSeenAt == null) { return 1; }
            if (mLastSeenAt == null) { return -1; }
            return mLastSeenAt.compareTo(otherProTeamPro.mLastSeenAt);
        }
        else
        {
            return 1;
        }
    }
}
