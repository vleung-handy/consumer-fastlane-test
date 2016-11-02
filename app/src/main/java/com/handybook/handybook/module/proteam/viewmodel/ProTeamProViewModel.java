package com.handybook.handybook.module.proteam.viewmodel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.module.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.module.proteam.model.ProTeamPro;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;

public class ProTeamProViewModel
{
    private final ProTeamPro mProTeamPro;
    private final ProviderMatchPreference mProviderMatchPreference;
    private final String mTitle;
    private final Float mAverageRating;
    private final Integer mJobsCount;
    private boolean mIsChecked;
    private boolean mIsHandymanIndicatorEnabled;
    private String mImageUrl;

    private ProTeamProViewModel(
            @NonNull final ProTeamPro proTeamPro,
            @NonNull final ProviderMatchPreference providerMatchPreference,
            final boolean shouldShowHandymanIndicators
    )
    {
        mProTeamPro = proTeamPro;
        mProviderMatchPreference = providerMatchPreference;
        mIsChecked = mProviderMatchPreference == ProviderMatchPreference.PREFERRED;
        mTitle = proTeamPro.getName();
        mAverageRating = proTeamPro.getAverageRating();
        mJobsCount = proTeamPro.getBookingCount();
        mIsHandymanIndicatorEnabled = shouldShowHandymanIndicators &&
                proTeamPro.getCategoryType() == ProTeamCategoryType.HANDYMEN;
        mImageUrl = proTeamPro.getImageUrl();
    }

    public static ProTeamProViewModel from(
            @NonNull final ProTeamPro proTeamPro,
            @NonNull final ProviderMatchPreference providerMatchPreference,
            final boolean shouldShowHandymanIndicators
    )
    {
        return new ProTeamProViewModel(
                proTeamPro,
                providerMatchPreference,
                shouldShowHandymanIndicators
        );
    }

    public ProviderMatchPreference getProviderMatchPreference()
    {
        return mProviderMatchPreference;
    }

    public ProTeamPro getProTeamPro()
    {
        return mProTeamPro;
    }

    public String getTitle()
    {
        return mTitle;
    }

    @Nullable
    public Float getAverageRating()
    {
        return mAverageRating;
    }

    @Nullable
    public Integer getJobsCount()
    {
        return mJobsCount;
    }

    public boolean isChecked()
    {
        return mIsChecked;
    }

    public void setChecked(final boolean checked)
    {
        mIsChecked = checked;
    }

    public String getImageUrl()
    {
        return mImageUrl;
    }

    public boolean isHandymanIndicatorEnabled()
    {
        return mIsHandymanIndicatorEnabled;
    }

    public interface OnInteractionListener
    {
        void onLongClick(ProTeamPro proTeamPro, ProviderMatchPreference providerMatchPreference);

        void onCheckedChanged(ProTeamPro proTeamPro, boolean checked);
    }
}
