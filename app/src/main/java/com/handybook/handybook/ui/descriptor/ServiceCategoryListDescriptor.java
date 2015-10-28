package com.handybook.handybook.ui.descriptor;

import com.handybook.handybook.R;

public enum ServiceCategoryListDescriptor
{
    HOME_CLEANING(
            R.string.home_cleaner,
            R.string.home_cleaner_slogan,
            R.drawable.ic_cleaner_fill,
            R.drawable.img_cleaner
    ),
    HANDYMAN(
            R.string.handyman,
            R.string.handyman_slogan,
            R.drawable.ic_handyman_fill,
            R.drawable.img_handyman
    ),
    PLUMBING(
            R.string.plumber,
            R.string.plumber_slogan,
            R.drawable.ic_plumber_fill,
            R.drawable.img_plumber
    ),
    ELECTRICIAN(
            R.string.electrician,
            R.string.electrician_slogan,
            R.drawable.ic_electrician_fill,
            R.drawable.img_electrician
    ),
    PAINTING(
            R.string.painter,
            R.string.painter_slogan,
            R.drawable.ic_painter_fill,
            R.drawable.img_painter
    ),;

    private final int mTitleString;
    private final int mSubtitleString;
    private final int mIconDrawable;
    private final int mImageDrawable;

    ServiceCategoryListDescriptor(int titleString, int subtitleString, int iconDrawable, int imageDrawable)
    {

        mTitleString = titleString;
        mSubtitleString = subtitleString;
        mIconDrawable = iconDrawable;
        mImageDrawable = imageDrawable;
    }

    public int getTitleString()
    {
        return mTitleString;
    }

    public int getSubtitleString()
    {
        return mSubtitleString;
    }

    public int getIconDrawable()
    {
        return mIconDrawable;
    }

    public int getImageDrawable()
    {
        return mImageDrawable;
    }
}
