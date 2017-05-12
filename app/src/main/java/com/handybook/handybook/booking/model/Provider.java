package com.handybook.handybook.booking.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.library.util.StringUtils;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;

import java.io.Serializable;
import java.util.Date;

public class Provider implements Serializable, Comparable<Provider> {

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
    @SerializedName("is_favorite")
    private boolean mIsFavorite;
    @SerializedName("team_type")
    private ProTeamCategoryType mCategoryType;
    @SerializedName("match_preference")
    private ProviderMatchPreference mMatchPreference;
    @SerializedName("layer_user_id")
    private String mLayerUserId;
    @SerializedName("last_seen_at")
    private Date mLastSeenAt;
    @SerializedName("profile_enabled")
    private boolean mProProfileEnabled;
    @SerializedName("instant_book_enabled")
    private boolean mIsInstantBookEnabled;

    public Provider(
            final int status,
            final String firstName,
            final String lastName,
            final String phone
    ) {
        mStatus = status;
        mFirstName = firstName;
        mLastName = lastName;
        mPhone = phone;
    }

    public boolean getIsProProfileEnabled() {
        return mProProfileEnabled;
    }

    public final int getStatus() {
        return mStatus;
    }

    public String getId() {
        return mId;
    }

    final void setStatus(final int status) {
        this.mStatus = status;
    }

    public String getName() { return mName; }

    public final String getFirstName() {
        return mFirstName;
    }

    final void setFirstName(final String firstName) {
        this.mFirstName = firstName;
    }

    public final String getLastName() {
        return mLastName;
    }

    final void setLastName(final String lastName) {
        this.mLastName = lastName;
    }

    public final String getPhone() {
        return mPhone;
    }

    final void setPhone(final String phone) {
        this.mPhone = phone;
    }

    public boolean isFavorite() { return mIsFavorite; }

    public ProTeamCategoryType getCategoryType() { return mCategoryType; }

    public String getLayerUserId() { return mLayerUserId; }

    /**
     * TODO temporary logic. eventually make the server return this
     * @return the provider's first name and last initial in the format: Aaaaa B.
     *
     */
    public String getFirstNameAndLastInitial() {
        String firstNameAndLastInitial = "";
        if (!Strings.isNullOrEmpty(mFirstName)) {
            firstNameAndLastInitial += StringUtils.capitalizeFirstCharacter(mFirstName);
        }
        if (!Strings.isNullOrEmpty(mLastName)) {
            firstNameAndLastInitial += (" " + Character.toUpperCase(mLastName.charAt(0)) + ".");
        }
        return firstNameAndLastInitial;
    }

    public final String getFullName() {
        return (mFirstName != null ? mFirstName : "") + " " + (mLastName != null ? mLastName : "");
    }

    @Nullable
    public Float getAverageRating() {
        return mAverageRating;
    }

    @Nullable
    public Integer getBookingCount() {
        return mBookingCount;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    @Nullable
    public ProviderMatchPreference getMatchPreference() {
        return mMatchPreference;
    }

    public boolean isInstantBookEnabled() {
        return mIsInstantBookEnabled;
    }

    public static final int PROVIDER_STATUS_ASSIGNED = 3;
    //TODO: Not sure what this is, just conjecturing based on code

    @Override
    public int compareTo(@NonNull final Provider otherPro) {
        if (mLastSeenAt == null && otherPro.mLastSeenAt == null) { return 0; }
        if (otherPro.mLastSeenAt == null) { return 1; }
        if (mLastSeenAt == null) { return -1; }
        return otherPro.mLastSeenAt.compareTo(mLastSeenAt);
    }
}
