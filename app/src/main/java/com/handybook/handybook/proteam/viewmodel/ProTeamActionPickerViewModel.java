package com.handybook.handybook.proteam.viewmodel;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.handybook.handybook.R;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;

import java.io.Serializable;
import java.util.List;

public class ProTeamActionPickerViewModel implements Serializable
{

    public enum ActionType
    {
        FAVORITE(ProviderMatchPreference.FAVORITE, R.string.favorite, R.color.handy_blue),
        UNFAVORITE(ProviderMatchPreference.PREFERRED, R.string.remove, R.color.error_red),
        BLOCK(ProviderMatchPreference.NEVER, R.string.block, R.color.error_red);

        private ProviderMatchPreference mMatchPreference;
        private final int mStringResId;
        private final int mColorResId;

        ActionType(
                final ProviderMatchPreference matchPreference,
                @StringRes final int stringResId,
                @ColorRes final int colorResId
        )
        {
            mMatchPreference = matchPreference;

            mStringResId = stringResId;
            mColorResId = colorResId;
        }

        public ProviderMatchPreference getMatchPreference()
        {
            return mMatchPreference;
        }

        public int getStringResId()
        {
            return mStringResId;
        }

        public int getColorResId()
        {
            return mColorResId;
        }
    }


    private int mProId;
    private String mProImageUrl;
    private String mTitle;
    private String mSubtitle;
    private final List<ActionType> mActionTypes;

    public ProTeamActionPickerViewModel(
            final int proId,
            @Nullable final String proImageUrl,
            @NonNull final String title,
            @Nullable final String subtitle,
            @NonNull final List<ActionType> actionTypes
    )
    {
        mProId = proId;
        mProImageUrl = proImageUrl;
        mTitle = title;
        mSubtitle = subtitle;
        mActionTypes = actionTypes;
    }

    public int getProId()
    {
        return mProId;
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
