package com.handybook.handybook.module.proteam.viewmodel;

import android.support.annotation.NonNull;

import com.handybook.handybook.module.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.module.proteam.model.ProTeamPro;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;

public class ProTeamProViewModel
{
    private static final String TEMPLATE_JOBS_COUNT_PLURAL = "%d jobs ";
    private static final String TEMPLATE_JOBS_COUNT_SINGULAR = "%d job";

    private final ProTeamPro mProTeamPro;
    private final ProviderMatchPreference mProviderMatchPreference;
    private final String mTitle;
    private final Float mAverageRating;
    private final Integer mJobsCount;
    private boolean mIsChecked;
    private String mImageUrl;

    private ProTeamProViewModel(
            @NonNull final ProTeamPro proTeamPro,
            @NonNull ProTeamCategoryType proTeamCategoryType,
            @NonNull ProviderMatchPreference providerMatchPreference
    )
    {
        mProTeamPro = proTeamPro;
        mProviderMatchPreference = providerMatchPreference;
        mIsChecked = mProviderMatchPreference == ProviderMatchPreference.PREFERRED;
        mTitle = proTeamPro.getName();
        mAverageRating = proTeamPro.getAverageRating();
        mJobsCount = proTeamPro.getBookingCount();
        mImageUrl = proTeamPro.getImageUrl();
    }

    public static ProTeamProViewModel from(
            @NonNull final ProTeamPro proTeamPro,
            @NonNull final ProTeamCategoryType proTeamCategoryType,
            @NonNull final ProviderMatchPreference providerMatchPreference
    )
    {
        return new ProTeamProViewModel(proTeamPro, proTeamCategoryType, providerMatchPreference);
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

    public Float getAverageRating()
    {
        return mAverageRating;
    }

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

    public boolean hasImageUrl()
    {
        return mImageUrl != null;
    }

    public interface OnInteractionListener
    {
        void onLongClick(ProTeamPro proTeamPro, ProviderMatchPreference providerMatchPreference);

        void onCheckedChanged(ProTeamPro proTeamPro, boolean checked);
    }
}
