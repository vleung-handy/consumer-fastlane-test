package com.handybook.handybook.library.ui.view.proteamcarousel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.referral.model.ProReferral;

import java.io.Serializable;
import java.text.DecimalFormat;

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

    public ProCarouselVM(
            final String imageUrl,
            final int jobCount,
            final float averageRating,
            final String displayName,
            final String buttonText
    ) {
        mImageUrl = imageUrl;
        mJobCount = jobCount;
        mAverageRating = averageRating;
        mDisplayName = displayName;
        mButtonText = buttonText;
        mActionable = true;
        mIsProTeam = true;
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
        mIsProTeam = proTeam;
    }

    public boolean isProTeam() {
        return mIsProTeam;
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
                provider.getImageUrl(),
                jobCount,
                rating,
                provider.getName(),
                proReferral.getReferralButtonText()
        );
    }

    @NonNull
    public static ProCarouselVM fromProvider(
            @NonNull final Provider provider,
            @Nonnull final String buttonText
    ) {
        return new ProCarouselVM(
                provider.getImageUrl(),
                provider.getBookingCount() == null ? 0 : provider.getBookingCount(),
                provider.getAverageRating() == null ? 0.0f : provider.getAverageRating(),
                provider.getFirstNameAndLastInitial(),
                buttonText
        );
    }
}
