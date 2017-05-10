package com.handybook.handybook.proprofiles.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.view.MiniProProfile;

import butterknife.Bind;

/**
 * this needs all the functionality of mini pro profile view so this extends it,
 * but has some layout differences and a new badge
 */
public class ProProfileSummaryView extends MiniProProfile {

    /**
     * this is not in the parent class because
     * it is not meant to show outside of pro profiles
     */
    @Bind(R.id.mini_pro_profile_no_reviews_indicator)
    View mNewToHandyIndicator;

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

    public void setNewToHandyIndicatorVisible(boolean visible) {
        mNewToHandyIndicator.setVisibility(visible ? VISIBLE : GONE);
    }
}
