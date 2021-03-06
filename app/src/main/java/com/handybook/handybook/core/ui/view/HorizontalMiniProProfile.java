package com.handybook.handybook.core.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.handybook.handybook.R;

public class HorizontalMiniProProfile extends BaseMiniProProfile {

    public HorizontalMiniProProfile(final Context context) {
        super(context);
    }

    public HorizontalMiniProProfile(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalMiniProProfile(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
    }

    public HorizontalMiniProProfile(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes
    ) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_horizontal_mini_pro_profile;
    }
}
