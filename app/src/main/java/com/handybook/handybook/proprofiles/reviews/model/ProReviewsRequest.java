package com.handybook.handybook.proprofiles.reviews.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * request for a page of reviews
 */
public class ProReviewsRequest {

    /**
     * represents the last review id of the current page of reviews
     */
    private String mCurrentPageLastReviewId;

    /**
     * the maximum number of reviews to return
     */
    private int mMaxReviewsPerPage;

    /**
     * the sort order of the reviews to return
     */
    @ProReviewsSortOrder
    private String mSortOrder;

    /**
     * the min rating of the reviews to return
     */
    private float mMinRating;

    public ProReviewsRequest(
            @Nullable String currentPageLastReviewId,
            int maxReviewsPerPage,
            @ProReviewsSortOrder String sortOrder,
            float minRating
    ) {
        mCurrentPageLastReviewId = currentPageLastReviewId;
        mMaxReviewsPerPage = maxReviewsPerPage;
        mSortOrder = sortOrder;
        mMinRating = minRating;
    }

    @Nullable
    public String getCurrentPageLastReviewId() {
        return mCurrentPageLastReviewId;
    }

    public int getMaxReviewsPerPage() {
        return mMaxReviewsPerPage;
    }

    @NonNull
    @ProReviewsSortOrder
    public String getSortOrder() {
        return mSortOrder;
    }

    public float getMinRating() {
        return mMinRating;
    }

    public static class SortOrder {

        public static final String DESCENDING = "desc";
        public static final String ASCENDING = "asc";
    }


    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
                       SortOrder.DESCENDING,
                       SortOrder.ASCENDING
               })
    public @interface ProReviewsSortOrder {
    }
}
