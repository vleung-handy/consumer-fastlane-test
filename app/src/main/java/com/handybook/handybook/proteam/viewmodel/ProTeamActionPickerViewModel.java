package com.handybook.handybook.proteam.viewmodel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.handybook.handybook.R;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;

import java.io.Serializable;
import java.util.List;

public class ProTeamActionPickerViewModel implements Serializable
{

    public enum ActionType
    {
        FAVORITE(ProviderMatchPreference.FAVORITE, R.string.favorite),
        UNFAVORITE(ProviderMatchPreference.PREFERRED, R.string.remove),
        BLOCK(ProviderMatchPreference.NEVER, R.string.block);

        private ProviderMatchPreference mMatchPreference;
        private final int mStringResId;

        ActionType(
                final ProviderMatchPreference matchPreference,
                @StringRes final int stringResId
        )
        {
            mMatchPreference = matchPreference;

            mStringResId = stringResId;
        }

        public ProviderMatchPreference getMatchPreference()
        {
            return mMatchPreference;
        }

        public int getStringResId()
        {
            return mStringResId;
        }
    }


    private int mProId;
    private ProTeamCategoryType mCategoryType;
    private String mProImageUrl;
    private String mTitle;
    private String mSubtitle;
    private final List<ActionType> mActionTypes;

    public ProTeamActionPickerViewModel(
            final int proId,
            final ProTeamCategoryType categoryType,
            @Nullable final String proImageUrl,
            @NonNull final String title,
            @Nullable final String subtitle,
            @NonNull final List<ActionType> actionTypes
    )
    {
        mProId = proId;
        mCategoryType = categoryType;
        mProImageUrl = proImageUrl;
        mTitle = title;
        mSubtitle = subtitle;
        mActionTypes = actionTypes;
    }

    public int getProId()
    {
        return mProId;
    }

    public ProTeamCategoryType getCategoryType()
    {
        return mCategoryType;
    }

    public String getProImageUrl()
    {
        return mProImageUrl;
    }

    public String getTitle()
    {
        return mTitle;
    }

    @Nullable
    public String getSubtitle()
    {
        return mSubtitle;
    }

    public List<ActionType> getActionTypes()
    {
        return mActionTypes;
    }
}
