package com.handybook.handybook.booking.rating;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public final class ReviewProRequest {

    @SerializedName("positive_feedback")
    private String mPositiveFeedback;
    @SerializedName("further_issues")
    private String mFurtherIssues;

    public ReviewProRequest(
            @Nullable final String positiveFeedback,
            @Nullable final String furtherIssues
    ) {
        mPositiveFeedback = positiveFeedback;
        mFurtherIssues = furtherIssues;
    }
}
