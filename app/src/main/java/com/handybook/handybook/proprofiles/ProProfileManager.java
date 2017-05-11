package com.handybook.handybook.proprofiles;

import android.support.annotation.NonNull;

import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.proprofiles.model.ProProfile;
import com.handybook.handybook.proprofiles.reviews.model.ProReviews;
import com.handybook.handybook.proprofiles.reviews.model.ProReviewsRequest;

import javax.inject.Inject;

public class ProProfileManager {

    private final DataManager mDataManager;

    @Inject
    public ProProfileManager(final DataManager dataManager) {
        mDataManager = dataManager;
    }

    public void getProviderProfile(
            @NonNull String providerId,
            DataManager.Callback<ProProfile> callback
    ) {
        mDataManager.getProviderProfile(providerId, callback);
    }

    public void getProviderReviews(
            @NonNull String providerId,
            @NonNull ProReviewsRequest proReviewsRequest,
            @NonNull DataManager.Callback<ProReviews> callback
    ) {
        mDataManager.getProviderReviews(providerId,
                                        proReviewsRequest,
                                        callback);
    }
}
