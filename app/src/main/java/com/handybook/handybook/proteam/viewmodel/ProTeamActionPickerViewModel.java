package com.handybook.handybook.proteam.viewmodel;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.handybook.handybook.R;

import java.io.Serializable;
import java.util.List;

public class ProTeamActionPickerViewModel implements Serializable
{

    public enum ActionType
    {
        FAVORITE(R.string.favorite, R.color.handy_blue),
        REMOVE(R.string.remove, R.color.error_red),
        BLOCK(R.string.block, R.color.error_red);

        private final int mStringResId;
        private final int mColorResId;

        ActionType(@StringRes final int stringResId, @ColorRes final int colorResId)
        {

            mStringResId = stringResId;
            mColorResId = colorResId;
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


    private String mProImageUrl;
    private String mTitle;
    private String mSubtitle;
    private final List<ActionType> mActionTypes;

    public ProTeamActionPickerViewModel(
            @Nullable final String proImageUrl,
            @NonNull final String title,
            @Nullable final String subtitle,
            @NonNull final List<ActionType> actionTypes
    )
    {
        mProImageUrl = proImageUrl;
        mTitle = title;
        mSubtitle = subtitle;
        mActionTypes = actionTypes;
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
