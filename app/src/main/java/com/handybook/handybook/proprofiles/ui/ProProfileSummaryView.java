package com.handybook.handybook.proprofiles.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.view.MiniProProfile;

/**
 * this needs all the functionality of mini pro profile view so this extends it,
 * but has some layout differences and may have functionality differences in the future
 */
public class ProProfileSummaryView extends MiniProProfile {

    public ProProfileSummaryView(final Context context) {
        super(context);
    }

    public ProProfileSummaryView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ProProfileSummaryView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_pro_profile_mini_summary;
    }
}
