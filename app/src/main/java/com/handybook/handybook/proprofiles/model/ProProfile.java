package com.handybook.handybook.proprofiles.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.referral.model.ProReferral;

import java.io.Serializable;

public class ProProfile implements Serializable {

    @SerializedName("provider_information")
    private ProviderInformation mProviderInformation;
    @SerializedName("regions")
    private String[] mProviderRegionsServed;
    @SerializedName("stats")
    private ProviderStats mProviderProviderStats;
    @SerializedName("referral")
    private ProReferral mReferralInfo;

    @NonNull
    public ProReferral getReferralInfo() {
        return mReferralInfo;
    }

    @NonNull
    public ProviderStats getProviderStats() {
        return mProviderProviderStats;
    }

    @NonNull
    public ProviderInformation getProviderInformation() {
        return mProviderInformation;
    }

    @Nullable
    public String[] getProviderRegionsServed() {
        return mProviderRegionsServed;
    }

    public static class ProviderStats implements Serializable {

        @SerializedName("years_handy_experience")
        private String mDurationExperienceHandyFormatted;
        @SerializedName("duration_pro_experience_before_handy")
        private String mDurationProExperienceFormatted;
        @SerializedName("background_checked")
        private Boolean mBackgroundChecked;

        @Nullable
        public String getDurationExperienceHandyFormatted() {
            return mDurationExperienceHandyFormatted;
        }

        @Nullable
        public String getDurationProExperienceFormatted() {
            return mDurationProExperienceFormatted;
        }

        @Nullable
        public Boolean getBackgroundChecked() {
            return mBackgroundChecked;
        }
    }

    public static class ProviderInformation implements Serializable {

        @SerializedName("id")
        private String mId;
        @SerializedName("name")
        private String mDisplayName;
        @SerializedName("first_name")
        private String mFirstName;
        @SerializedName("last_name")
        private String mLastName;
        @SerializedName("average_rating")
        private Float mAverageRating;
        @SerializedName("booking_count")
        private Integer mBookingCount;
        @SerializedName("profile_images")
        private ProfileImage[] mProfileImages;
        @SerializedName("profile_photo_url")
        private String mProfilePhotoUrl;
        @SerializedName("match_preference")
        private ProviderMatchPreference mMatchPreference;
        @SerializedName("is_favorite")
        private Boolean mIsCustomerFavorite;
        @SerializedName("referral_code")
        private String mReferralCode;
        @SerializedName("team_type")
        private String mProTeamCategoryType;

        @Nullable
        public ProfileImage getProfileImage(@NonNull final ProfileImage.Type type) {
            if (mProfileImages != null) {
                for (ProfileImage profileImage : mProfileImages) {
                    if (profileImage.getType() == type) {
                        return profileImage;
                    }
                }
            }
            return null;
        }

        @Nullable
        public ProTeamCategoryType getProTeamCategoryType() {
            try
            {
                return ProTeamCategoryType.valueOf(mProTeamCategoryType);
            }
            catch (Exception e)
            {
                Crashlytics.logException(e);
            }
            return null;
        }

        public String getProfilePhotoUrl() {
            return mProfilePhotoUrl;
        }

        public String getReferralCode() {
            return mReferralCode;
        }

        @Nullable
        public Boolean isCustomerFavorite() {
            return mIsCustomerFavorite;
        }

        @NonNull
        public ProviderMatchPreference getMatchPreference() {
            return mMatchPreference;
        }

        @NonNull
        public String getId() {
            return mId;
        }

        @Nullable
        public String getDisplayName() {
            return mDisplayName;
        }

        @Nullable
        public String getFirstName() {
            return mFirstName;
        }

        public String getLastName() {
            return mLastName;
        }

        @Nullable
        public Float getAverageRating() {
            return mAverageRating;
        }

        @Nullable
        public Integer getBookingCount() {
            return mBookingCount;
        }

        @Nullable
        public ProfileImage[] getProfileImages() {
            return mProfileImages;
        }

        public static class ProfileImage implements Serializable {
            public enum Type {
                @SerializedName("original")
                ORIGINAL,
                @SerializedName("thumbnail")
                THUMBNAIL,
                @SerializedName("small")
                SMALL,
                @SerializedName("medium")
                MEDIUM,
                @SerializedName("large")
                LARGE,
            }

            @SerializedName("url")
            private String mUrl;
            @SerializedName("type")
            private Type mType;

            @NonNull
            public Type getType() {
                return mType;
            }

            @Nullable
            public String getUrl() {
                return mUrl;
            }
        }
    }
}
