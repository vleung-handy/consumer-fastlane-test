package com.handybook.handybook.module.proteam.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Object describing one provider in a ProTeam
 */
public class ProTeamPro implements Serializable, Comparable
{
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

    public int getBookingCount()
    {
        return mBookingCount;
    }

    public String getImageUrl()
    {
        return mImageUrl;
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
            return otherProTeamPro.mLastSeenAt.compareTo(mLastSeenAt);
        }
        else
        {
            return 1;
        }
    }
}
