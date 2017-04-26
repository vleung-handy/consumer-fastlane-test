package com.handybook.handybook.proprofiles;

import android.content.Context;
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
            Context context,//todo remove
            @NonNull String providerId,
            DataManager.Callback<ProProfile> callback
    ) {
        mDataManager.getProviderProfile(providerId, callback);
        //todo remove, test only
//        try {
//            String json = IOUtils.loadJSONFromAsset(context, "test_provider_profile.json");
//            ProProfile proProfile = new GsonBuilder().setDateFormat(
//                    DateTimeUtils.UNIVERSAL_DATE_FORMAT
//            ).create().fromJson(json, ProProfile.class);
//            callback.onSuccess(proProfile);
//            return;
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//        callback.onError(null);
    }

    public void getMoreProviderReviews(
            Context context,//todo remove
            @NonNull String providerId,
            @NonNull ProReviewsRequest proReviewsRequest,
            @NonNull DataManager.Callback<ProReviews> callback
    ) {
        mDataManager.getProviderReviews(providerId,
                                        proReviewsRequest,
                                        callback);

        //todo remove, test only
//        try {
//            String json = IOUtils.loadJSONFromAsset(context, "test_provider_reviews.json");
//            ProReviews proReviews = new GsonBuilder().setDateFormat(
//                    DateTimeUtils.UNIVERSAL_DATE_FORMAT
//            ).create().fromJson(json, ProReviews.class);
//            callback.onSuccess(proReviews);
//            return;
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//        callback.onError(null);
    }
}
