package com.handybook.handybook.proteam.mypros;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.proteam.util.ProTeamUtils;

import java.io.Serializable;

/**
 * A model to use with the {@link ProTeamRecyclerViewAdapter}
 */
public class ProTeamRecyclerViewItemVM implements Serializable {

    private String mImageUrl;
    private Integer mJobCount;
    private Float mAverageRating;
    private String mDisplayName;
    private String mProTeamCategoryText;
    private boolean mIsProTeam;
    private boolean mIsFavorite;
    private boolean mIsProfileEnabled;

    /**
     * needed for callbacks to identify the provider
     *
     * the adapter that uses this view model has no way of getting this otherwise
     */
    private String mProviderId;

    private ProTeamRecyclerViewItemVM(
            @NonNull final String providerId,
            final String imageUrl,
            @Nullable final Integer jobCount,
            @Nullable final Float averageRating,
            final String displayName,
            @Nullable final String proTeamCategoryText,
            final boolean isProfileEnabled,
            @Nullable final Boolean isProTeam,
            @Nullable final Boolean isFavorite
    ) {
        mProviderId = providerId;
        mImageUrl = imageUrl;
        mJobCount = jobCount;
        mAverageRating = averageRating;
        mDisplayName = displayName;
        mProTeamCategoryText = proTeamCategoryText;
        mIsProfileEnabled = isProfileEnabled;
        mIsProTeam = isProTeam == null ? false : isProTeam;
        mIsFavorite = isFavorite == null ? false : isFavorite;
    }

    String getProTeamCategoryText() {
        return mProTeamCategoryText;
    }

    boolean isProTeam() {
        return mIsProTeam;
    }

    boolean isProfileEnabled() {
        return mIsProfileEnabled;
    }

    @NonNull
    public String getProviderId() {
        return mProviderId;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    @Nullable
    Integer getJobCount() {
        return mJobCount;
    }

    @Nullable
    public Float getAverageRating() {
        return mAverageRating;
    }

    String getDisplayName() {
        return mDisplayName;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    @NonNull
    static ProTeamRecyclerViewItemVM fromProvider(
            @NonNull final Provider provider
    ) {
        return new ProTeamRecyclerViewItemVM(
                provider.getId(),
                provider.getImageUrl(),
                provider.getBookingCount(),
                provider.getAverageRating(),
                provider.getFirstNameAndLastInitial(),
                String.valueOf(provider.getCategoryType()),
                provider.getIsProProfileEnabled(),
                ProTeamUtils.isProOnProTeam(provider.getMatchPreference()),
                provider.isFavorite()
        );
    }
}
