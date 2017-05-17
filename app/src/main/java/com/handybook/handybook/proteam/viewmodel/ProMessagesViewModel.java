package com.handybook.handybook.proteam.viewmodel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;

import java.io.Serializable;

public class ProMessagesViewModel implements Serializable {

    private final String mProviderId;
    private final String mProviderName;
    private final String mProviderFirstName;
    private final String mImageUrl;
    private final ProTeamCategoryType mProTeamCategoryType;
    private final boolean mIsFavorite;
    private final boolean mIsProProfileEnabled;

    public ProMessagesViewModel(@NonNull final Provider provider) {
        this(
                provider.getId(),
                provider.getName(),
                provider.getFirstName(),
                provider.getImageUrl(),
                provider.getCategoryType(),
                provider.isFavorite(),
                provider.getIsProProfileEnabled()
        );
    }

    /**
     *
     * @param providerId
     * @param providerName
     * @param providerFirstName
     * @param imageUrl
     * @param proCategoryType
     * @param isFavorite This is boolean because it could be null. Casting to boolean will fail
     * @param isProProfileEnabled
     */
    public ProMessagesViewModel(
            @NonNull final String providerId,
            @Nullable final String providerName,
            @Nullable final String providerFirstName,
            @Nullable final String imageUrl,
            @Nullable final ProTeamCategoryType proCategoryType,
            @Nullable final Boolean isFavorite,
            final boolean isProProfileEnabled
    ) {
        mProviderId = providerId;
        mProviderName = providerName;
        mProviderFirstName = providerFirstName;
        mImageUrl = imageUrl;
        mProTeamCategoryType = proCategoryType;
        mIsFavorite = isFavorite != null && isFavorite;
        mIsProProfileEnabled = isProProfileEnabled;
    }

    public boolean isProProfileEnabled() {
        return mIsProProfileEnabled;
    }

    @NonNull
    public String getProviderId() { return mProviderId; }

    @Nullable
    public String getProviderName() { return mProviderName; }

    @Nullable
    public String getProviderFirstName() { return mProviderFirstName; }

    @Nullable
    public String getImageUrl() { return mImageUrl; }

    @Nullable
    public ProTeamCategoryType getCategoryType() { return mProTeamCategoryType; }

    public boolean isFavorite() { return mIsFavorite; }
}
