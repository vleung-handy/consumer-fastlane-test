package com.handybook.handybook.library.ui.view.proteamcarousel;

import java.io.Serializable;

/**
 * A model to use with the {@link ProTeamCarousel}
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
}
