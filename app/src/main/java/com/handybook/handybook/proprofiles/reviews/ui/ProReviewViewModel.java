package com.handybook.handybook.proprofiles.reviews.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.proprofiles.reviews.model.ProReviews;

import java.text.SimpleDateFormat;
import java.util.Locale;

class ProReviewViewModel {

    private ProReviews.Review mProviderReview;

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

    public String getLocationText() {
        return mProviderReview.getLocationText();
    }

    @NonNull
    String getDateText() {
        //todo parameterize
        return new SimpleDateFormat(
                "MMM yyyy",
                Locale.getDefault()
        ).format(mProviderReview.getDate());
    }

    @NonNull
    public Float getRating() {
        return mProviderReview.getRating();
    }

}
