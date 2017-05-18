package com.handybook.handybook.core.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.view.BaseMiniProProfile;

/**
 * currently only used by the share page, but will eventually be also used by the new "my pros" tab
 */
public class VerticalMiniProProfile extends BaseMiniProProfile {

    public VerticalMiniProProfile(final Context context) {
        super(context);
    }

    public VerticalMiniProProfile(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalMiniProProfile(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
    }

    public VerticalMiniProProfile(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes
    ) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_vertical_mini_pro_profile;
    }
}
