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
    private String[] mRegions;
    @SerializedName("stats")
    private Stats mStats;
    @SerializedName("referral")
    private ProReferral mProReferral;

    public ProReferral getProReferral() {
        return mProReferral;
    }

    public Stats getStats() {
        return mStats;
    }

    public ProviderInformation getProviderInformation() {
        return mProviderInformation;
    }

    public String[] getRegions() {
        return mRegions;
    }


    public static class Stats implements Serializable {

        @SerializedName("years_handy_experience")
        private String mDurationExperienceHandyFormatted;
        @SerializedName("duration_pro_experience_before_handy")
        private String mDurationProExperienceFormatted;
        @SerializedName("background_checked")
        private Boolean mBackgroundChecked;

        public String getDurationExperienceHandyFormatted() {
            return mDurationExperienceHandyFormatted;
        }

        public String getDurationProExperienceFormatted() {
            return mDurationProExperienceFormatted;
        }

        public Boolean getBackgroundChecked() {
            return mBackgroundChecked;
        }
    }
    public static class ProviderInformation implements Serializable {

        @SerializedName("id")
        private Integer mId;
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

        public Boolean isCustomerFavorite() {
            return mIsCustomerFavorite;
        }

        public ProviderMatchPreference getMatchPreference() {
            return mMatchPreference;
        }

        public Integer getId() {
            return mId;
        }

        public String getDisplayName() {
            return mDisplayName;
        }

        public String getFirstName() {
            return mFirstName;
        }

        public String getLastName() {
            return mLastName;
        }

        public Float getAverageRating() {
            return mAverageRating;
        }

        public Integer getBookingCount() {
            return mBookingCount;
        }

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

            public Type getType() {
                return mType;
            }

            public String getUrl() {
                return mUrl;
            }
        }
    }
}
