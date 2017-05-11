package com.handybook.handybook.proprofiles.reviews.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * a page of reviews. response to {@link ProReviewsRequest}
 */
public class ProReviews implements Serializable {

    @SerializedName("reviews")
    private Review[] mReviews;
    /**
     * represents the last review id, that should be sent
     * in the next {@link ProReviewsRequest} to get the next page of reviews
     */
    @SerializedName("starting_after_id")
    private String mLastReviewId;

    @Nullable
    public Review[] getReviews() {
        return mReviews;
    }

    @Nullable
    public String getLastReviewId() {
        return mLastReviewId;
    }


    public static class Review implements Serializable
    {
        /**
         * server unwilling to send String
         * and we cannot parse an int as String
         */
        @SerializedName("id")
        private String mId;
        @SerializedName("rating")
        private Float mRating;
        @SerializedName("date")
        private Date mDate;
        @SerializedName("text")
        private String mText;
        @SerializedName("location_text")
        private String mLocationText;

        @NonNull
        public String getId() {
            return mId;
        }

        @NonNull
        public Float getRating() {
            return mRating;
        }

        @NonNull
        public Date getDate() {
            return mDate;
        }

        @Nullable
        public String getText() {
            return mText;
        }

        @Nullable
        public String getLocationText() {
            return mLocationText;
        }
    }
}
