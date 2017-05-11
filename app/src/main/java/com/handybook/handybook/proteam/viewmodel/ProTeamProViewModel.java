package com.handybook.handybook.proteam.viewmodel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.layer.sdk.messaging.Conversation;

import java.io.Serializable;

public class ProTeamProViewModel implements Serializable {

    private final Provider mProTeamPro;
    private final ProviderMatchPreference mProviderMatchPreference;
    private final String mTitle;
    private final Float mAverageRating;
    private final Integer mJobsCount;
    private boolean mIsChecked;
    private boolean mIsHandymanIndicatorEnabled;
    private String mImageUrl;
    transient private Conversation mConversation; // Be careful!
    private boolean mIsFavorite;
    private boolean mIsInstantBookIndicatorEnabled;

    private ProTeamProViewModel(
            @NonNull final Provider proTeamPro,
            @NonNull final ProviderMatchPreference providerMatchPreference,
            final boolean shouldShowHandymanIndicators,
            final boolean shouldShowInstantBookIndicator
    ) {
        mProTeamPro = proTeamPro;
        mProviderMatchPreference = providerMatchPreference;
        mIsChecked = mProviderMatchPreference == ProviderMatchPreference.PREFERRED;
        mTitle = proTeamPro.getName();
        mAverageRating = proTeamPro.getAverageRating();
        mJobsCount = proTeamPro.getBookingCount();
        mIsHandymanIndicatorEnabled = shouldShowHandymanIndicators &&
                                      proTeamPro.getCategoryType() == ProTeamCategoryType.HANDYMEN;
        mIsInstantBookIndicatorEnabled = shouldShowInstantBookIndicator;
        mImageUrl = proTeamPro.getImageUrl();
        mIsFavorite = proTeamPro.isFavorite();
    }

    public static ProTeamProViewModel from(
            @NonNull final Provider proTeamPro,
            @NonNull final ProviderMatchPreference providerMatchPreference,
            final boolean shouldShowHandymanIndicators
    ) {
        return from(proTeamPro, providerMatchPreference, shouldShowHandymanIndicators, false);
    }

    public static ProTeamProViewModel from(
            @NonNull final Provider proTeamPro,
            @NonNull final ProviderMatchPreference providerMatchPreference,
            final boolean shouldShowHandymanIndicators,
            final boolean shouldShowInstantBookIndicator
    ) {
        return new ProTeamProViewModel(
                proTeamPro,
                providerMatchPreference,
                shouldShowHandymanIndicators,
                shouldShowInstantBookIndicator
        );
    }

    public ProviderMatchPreference getProviderMatchPreference() {
        return mProviderMatchPreference;
    }

    @NonNull
    public Provider getProTeamPro() {
        return mProTeamPro;
    }

    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public Float getAverageRating() {
        return mAverageRating;
    }

    @Nullable
    public Integer getJobsCount() {
        return mJobsCount;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(final boolean checked) {
        mIsChecked = checked;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public boolean isHandymanIndicatorEnabled() {
        return mIsHandymanIndicatorEnabled;
    }

    public boolean isInstantBookIndicatorEnabled() {
        return mIsInstantBookIndicatorEnabled;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public interface OnInteractionListener {

        void onLongClick(Provider proTeamPro, ProviderMatchPreference providerMatchPreference);

        void onCheckedChanged(Provider proTeamPro, boolean checked);
    }

    public Conversation getConversation() {
        return mConversation;
    }

    public void setConversation(final Conversation conversation) {
        mConversation = conversation;
    }
}
