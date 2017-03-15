package com.handybook.handybook.library.ui.view.proteamcarousel;

import android.support.annotation.NonNull;

import com.handybook.handybook.referral.model.ProReferral;

import java.io.Serializable;

/**
 * A model to use with the {@link ProTeamCarouselView}
 */
public class ProCarouselVM implements Serializable {

    private String mImageUrl;
    private String mJobCount;
    private String mAverageRating;
    private String mDisplayName;

    public ProCarouselVM(
            final String imageUrl,
            final String jobCount,
            final String averageRating,
            final String displayName
    ) {
        mImageUrl = imageUrl;
        mJobCount = jobCount;
        mAverageRating = averageRating;
        mDisplayName = displayName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getJobCount() {
        return mJobCount;
    }

    public String getAverageRating() {
        return mAverageRating;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public static ProCarouselVM fromProReferral(@NonNull final ProReferral proReferral) {
        if (proReferral.getProvider() == null) {
            return null;
        }

        ProCarouselVM model = new ProCarouselVM(
                proReferral.getProvider().getImageUrl(),
                String.valueOf(proReferral.getProvider().getBookingCount()),
                String.valueOf(proReferral.getProvider().getAverageRating()),
                proReferral.getProvider().getName()
        );

        return model;
    }
}
