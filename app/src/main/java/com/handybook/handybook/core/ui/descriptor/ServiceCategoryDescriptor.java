package com.handybook.handybook.core.ui.descriptor;

import com.handybook.handybook.R;

public enum ServiceCategoryDescriptor {
    HANDYMAN(
            R.string.handyman,
            R.string.handyman_slogan_long,
            R.drawable.ic_handyman_fill,
            R.color.handy_service_handyman,
            R.color.handy_service_handyman_darkened,
            R.drawable.bg_ripple_handyman
    ),
    PLUMBING(
            R.string.plumber,
            R.string.plumber_slogan_long,
            R.drawable.ic_plumber_fill,
            R.color.handy_service_plumber,
            R.color.handy_service_plumber_darkened,
            R.drawable.bg_ripple_plumber
    ),
    ELECTRICIAN(
            R.string.electrician,
            R.string.electrician_slogan_long,
            R.drawable.ic_electrician_fill,
            R.color.handy_service_electrician,
            R.color.handy_service_electrician_darkened,
            R.drawable.bg_ripple_electrician
    ),;

    private final int mTitle;
    private final int mSlogan;
    private final int mIcon;
    private final int mColor;
    private final int mColorDark;
    private int mBackground;

    ServiceCategoryDescriptor(
            int title,
            int slogan,
            int icon,
            int color,
            int colorDark,
            int background
    ) {
        mTitle = title;
        mSlogan = slogan;
        mIcon = icon;
        mColor = color;
        mColorDark = colorDark;
        mBackground = background;
    }

    public int getTitle() {
        return mTitle;
    }

    public int getIcon() {
        return mIcon;
    }

    public int getSlogan() {
        return mSlogan;
    }

    public int getColor() {
        return mColor;
    }

    public int getColorDark() {
        return mColorDark;
    }

    public int getBackground() {
        return mBackground;
    }
}
