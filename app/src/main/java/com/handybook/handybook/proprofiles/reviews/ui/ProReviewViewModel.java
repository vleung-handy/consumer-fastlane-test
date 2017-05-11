package com.handybook.handybook.proprofiles.reviews.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.proprofiles.reviews.model.ProReviews;

class ProReviewViewModel {

    private final ProReviews.Review mProviderReview;

    ProReviewViewModel(@NonNull ProReviews.Review providerReview) {
        mProviderReview = providerReview;
    }

    @NonNull
    ProReviews.Review getProviderReview() {
        return mProviderReview;
    }

    @Nullable
    String getReviewText() {
        return mProviderReview.getText();
    }

    @Nullable
    public String getLocationText() {
        return mProviderReview.getLocationText();
    }

    @NonNull
    String getDateText() {
        return DateTimeUtils.MONTH_YEAR_FORMATTER.format(mProviderReview.getDate());
    }

    @NonNull
    public Float getRating() {
        return mProviderReview.getRating();
    }

}
