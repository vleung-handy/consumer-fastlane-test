package com.handybook.handybook.proteam.viewmodel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;

import java.io.Serializable;

public class ProMessagesViewModel implements Serializable {

    private final String mProviderId;
    private final String mProName;
    private final String mImageUrl;
    private final ProTeamCategoryType mProTeamCategoryType;
    private final boolean mIsFavorite;

    public ProMessagesViewModel(@NonNull final Provider pro) {
        this(
                String.valueOf(pro.getId()),
                pro.getName(),
                pro.getImageUrl(),
                pro.getCategoryType(),
                pro.isFavorite()
        );
    }

    public ProMessagesViewModel(
            @Nullable final String providerId,
            @Nullable final String proName,
            @Nullable final String imageUrl,
            @Nullable final ProTeamCategoryType proCategoryType,
            final boolean isFavorite
    ) {
        mProviderId = providerId;
        mImageUrl = imageUrl;
        mProName = proName;
        mIsFavorite = isFavorite;
        mProTeamCategoryType = proCategoryType;
    }

    @Nullable
    public String getProviderId() { return mProviderId; }

    @Nullable
    public String getProName() { return mProName; }

    @Nullable
    public String getImageUrl() { return mImageUrl; }

    @Nullable
    public ProTeamCategoryType getCategoryType() { return mProTeamCategoryType; }

    public boolean isFavorite() { return mIsFavorite; }
}
