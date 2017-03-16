package com.handybook.handybook.core.manager;

import android.content.Context;
import android.support.annotation.NonNull;

import com.handybook.handybook.configuration.manager.ConfigurationManager;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.library.util.NetworkUtils;

import java.util.concurrent.TimeUnit;

/**
 * responsible for managing configs and prefs
 * related to reviewing the app in the play store
 */
public class ReviewAppManager {

    private final Context mContext;
    private final ConfigurationManager mConfigurationManager;
    private final DefaultPreferencesManager mDefaultPreferencesManager;

    public ReviewAppManager(
            @NonNull Context context,
            @NonNull final ConfigurationManager configurationManager,
            @NonNull final DefaultPreferencesManager preferenceManager
    ) {
        mContext = context;
        mConfigurationManager = configurationManager;
        mDefaultPreferencesManager = preferenceManager;
    }

    public boolean shouldDisplayReviewAppBanner() {
        Configuration.ReviewAppBanner reviewAppBannerConfig
                = mConfigurationManager.getPersistentConfiguration().getReviewAppBanner();
        return reviewAppBannerConfig != null
               && reviewAppBannerConfig.isEnabled()
               && !wasAppReviewed()
               && hasMinDelayPassedSinceAppReviewLastDeclined()
               && NetworkUtils.isConnectedToInternet(mContext);
    }

    public void updateReviewAppBannerDeclinedTime() {
        mDefaultPreferencesManager.setLong(
                PrefsKey.REVIEW_APP_BANNER_LAST_DECLINED_TIME_MS,
                System.currentTimeMillis()
        );
    }

    public void markAppAsRated() {
        mDefaultPreferencesManager.setBoolean(PrefsKey.APP_REVIEWED, true);
    }

    /**
     * @return true if the min delay time passed since the app review request was last declined
     */
    private boolean hasMinDelayPassedSinceAppReviewLastDeclined() {
        Configuration.ReviewAppBanner reviewAppBannerConfig
                = mConfigurationManager.getPersistentConfiguration().getReviewAppBanner();
        if (reviewAppBannerConfig == null) {
            return false;
        }
        Integer displayDelayDaysAfterDeclined
                = reviewAppBannerConfig.getDisplayDelayDaysAfterDeclined();
        if (displayDelayDaysAfterDeclined == null) {
            //if no display delay given act like the delay is infinite
            return false;
        }

        long displayDelayMsAfterDeclined = TimeUnit.DAYS.toMillis(displayDelayDaysAfterDeclined);
        return System.currentTimeMillis() - getRateAppBannerLastDeclinedTimeMs() >=
               displayDelayMsAfterDeclined;
    }

    private long getRateAppBannerLastDeclinedTimeMs() {
        return mDefaultPreferencesManager.getLong(
                PrefsKey.REVIEW_APP_BANNER_LAST_DECLINED_TIME_MS,
                0
        );
    }

    private boolean wasAppReviewed() {
        return mDefaultPreferencesManager.getBoolean(PrefsKey.APP_REVIEWED, false);
    }
}
