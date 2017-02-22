package com.handybook.handybook.booking.model;


import android.support.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.library.util.StringUtils;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;

import java.io.Serializable;

public class Provider implements Serializable
{
    @SerializedName("status")
    private int mStatus;
    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("first_name")
    private String mFirstName;
    @SerializedName("last_name")
    private String mLastName;
    @SerializedName("phone")
    private String mPhone;
    @SerializedName("average_rating")
    private Float mAverageRating;
    @SerializedName("booking_count")
    private Integer mBookingCount;
    @SerializedName("profile_photo_url")
    private String mImageUrl;
    @SerializedName("chat_enabled")
    private boolean mChatEnabled;
    @SerializedName("is_favorite")
    private boolean mIsFavorite;
    @SerializedName("team_type")
    private ProTeamCategoryType mCategoryType;
    @SerializedName("layer_user_id")
    private String mLayerUserId;

    public Provider(final int status, final String firstName, final String lastName, final String phone)
    {
        mStatus = status;
        mFirstName = firstName;
        mLastName = lastName;
        mPhone = phone;
    }

    public final int getStatus()
    {
        return mStatus;
    }

    public String getId()
    {
        return mId;
    }

    final void setStatus(final int status)
    {
        this.mStatus = status;
    }

    public String getName() { return mName; }

    public final String getFirstName()
    {
        return mFirstName;
    }

    final void setFirstName(final String firstName)
    {
        this.mFirstName = firstName;
    }

    public final String getLastName()
    {
        return mLastName;
    }

    final void setLastName(final String lastName)
    {
        this.mLastName = lastName;
    }

    public final String getPhone()
    {
        return mPhone;
    }

    final void setPhone(final String phone)
    {
        this.mPhone = phone;
    }

    public boolean isChatEnabled() { return mChatEnabled; }

    public boolean isFavorite() { return mIsFavorite; }

    public ProTeamCategoryType getCategoryType() { return mCategoryType; }

    public String getLayerUserId() { return mLayerUserId; }

    /**
     * TODO temporary logic. eventually make the server return this
     * @return the provider's first name and last initial in the format: Aaaaa B.
     *
     */
    public String getFirstNameAndLastInitial()
    {
        String firstNameAndLastInitial = "";
        if (!Strings.isNullOrEmpty(mFirstName))
        {
            firstNameAndLastInitial += StringUtils.capitalizeFirstCharacter(mFirstName);
        }
        if (!Strings.isNullOrEmpty(mLastName))
        {
            firstNameAndLastInitial += (" " + Character.toUpperCase(mLastName.charAt(0)) + ".");
        }
        return firstNameAndLastInitial;
    }

    public final String getFullName()
    {
        return (mFirstName != null ? mFirstName : "") + " " + (mLastName != null ? mLastName : "");
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

    public static final int PROVIDER_STATUS_ASSIGNED = 3; //TODO: Not sure what this is, just conjecturing based on code
}
