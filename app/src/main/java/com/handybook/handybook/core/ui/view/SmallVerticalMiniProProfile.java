package com.handybook.handybook.core.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.handybook.handybook.R;

public class SmallVerticalMiniProProfile extends BaseMiniProProfile {

    public SmallVerticalMiniProProfile(final Context context) {
        super(context);
    }

    public SmallVerticalMiniProProfile(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public SmallVerticalMiniProProfile(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
    }

    public SmallVerticalMiniProProfile(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes
    ) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_small_vertical_mini_pro_profile;
    }
}
