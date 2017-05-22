package com.handybook.handybook.library.ui.view.proteamcarousel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.proteam.util.ProTeamUtils;
import com.handybook.handybook.referral.model.ProReferral;

import java.io.Serializable;

import javax.annotation.Nonnull;

/**
 * A model to use with the {@link ProTeamCarouselView}
 */
public class ProCarouselVM implements Serializable {

    private String mImageUrl;
    private int mJobCount;
    private float mAverageRating;
    private String mDisplayName;
    private String mButtonText;
    private boolean mActionable;
    private boolean mIsProTeam;
    private boolean mIsFavorite;
    private boolean mIsProfileEnabled;

    /**
     * needed for callbacks to identify the provider
     *
     * the adapter that uses this view model has no way of getting this otherwise
     */
    private String mProviderId;

    public ProCarouselVM(
            final String providerId,
            final String imageUrl,
            final int jobCount,
            final float averageRating,
            final String displayName,
            final String buttonText,
            final boolean isProfileEnabled,
            final boolean isProTeam,
            final boolean isFavorite
    ) {
        mProviderId = providerId;
        mImageUrl = imageUrl;
        mJobCount = jobCount;
        mAverageRating = averageRating;
        mDisplayName = displayName;
        mButtonText = buttonText;
        mActionable = true;
        mIsProTeam = isProTeam;
        mIsFavorite = isFavorite;
        mIsProfileEnabled = isProfileEnabled;
    }

    public boolean isProTeam() {
        return mIsProTeam;
    }

    public boolean isProfileEnabled() {
        return mIsProfileEnabled;
    }

    public String getProviderId() {
        return mProviderId;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public int getJobCount() {
        return mJobCount;
    }

    public float getAverageRating() {
        return mAverageRating;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setButtonText(final String buttonText) {
        mButtonText = buttonText;
    }

    @Nullable
    public String getButtonText() {
        return mButtonText;
    }

    public void setActionable(final boolean actionable) {
        mActionable = actionable;
    }

    public boolean isActionable() {
        return mActionable;
    }

    public void setIsProTeam(final boolean proTeam) {
        mIsFavorite = proTeam;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public static ProCarouselVM fromProReferral(@NonNull final ProReferral proReferral) {
        final Provider provider = proReferral.getProvider();
        if (provider == null) {
            return null;
        }

        int jobCount = provider.getBookingCount() == null ? 0 : provider.getBookingCount();
        float rating = provider.getAverageRating() == null
                       ? 0.f
                       : provider.getAverageRating();

        return new ProCarouselVM(
                provider.getId(),
                provider.getImageUrl(),
                jobCount,
                rating,
                provider.getName(),
                proReferral.getReferralButtonText(),
                provider.getIsProProfileEnabled(),
                ProTeamUtils.isProOnProTeam(provider.getMatchPreference()),
                provider.isFavorite()
        );
    }

    @NonNull
    public static ProCarouselVM fromProvider(
            @NonNull final Provider provider,
            @Nonnull final String buttonText
    ) {
        return new ProCarouselVM(
                provider.getId(),
                provider.getImageUrl(),
                provider.getBookingCount() == null ? 0 : provider.getBookingCount(),
                provider.getAverageRating() == null ? 0.0f : provider.getAverageRating(),
                provider.getFirstNameAndLastInitial(),
                buttonText,
                provider.getIsProProfileEnabled(),
                ProTeamUtils.isProOnProTeam(provider.getMatchPreference()),
                provider.isFavorite()
        );
    }
}
