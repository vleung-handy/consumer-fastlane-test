package com.handybook.handybook.library.ui.view.proteamcarousel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.referral.model.ProReferral;

import java.io.Serializable;

/**
 * A model to use with the {@link ProTeamCarouselView}
 */
public class ProCarouselVM implements Serializable {

    private String mImageUrl;
    private int mJobCount;
    private String mAverageRating;
    private String mDisplayName;
    private String mButtonText;

    public ProCarouselVM(
            final String imageUrl,
            final int jobCount,
            final String averageRating,
            final String displayName,
            final String buttonText
    ) {
        mImageUrl = imageUrl;
        mJobCount = jobCount;
        mAverageRating = averageRating;
        mDisplayName = displayName;
        mButtonText = buttonText;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public int getJobCount() {
        return mJobCount;
    }

    public String getAverageRating() {
        return mAverageRating;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    @Nullable
    public String getButtonText() {
        return mButtonText;
    }

    public static ProCarouselVM fromProReferral(@NonNull final ProReferral proReferral) {
        if (proReferral.getProvider() == null) {
            return null;
        }

        int jobCount = proReferral.getProvider().getBookingCount() == null
                       ? 0
                       : proReferral.getProvider().getBookingCount();
        String averageRating = proReferral.getProvider().getAverageRating() == null
                               ? null
                               : String.valueOf(proReferral.getProvider().getAverageRating());
        ProCarouselVM model = new ProCarouselVM(
                proReferral.getProvider().getImageUrl(),
                jobCount,
                averageRating,
                proReferral.getProvider().getName(),
                proReferral.getReferralButtonText()
        );

        return model;
    }
}
