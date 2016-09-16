package com.handybook.handybook.ui.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.FrameLayout;

import com.handybook.handybook.R;

import butterknife.ButterKnife;

/**
 */
public class ShareBannerView extends FrameLayout
{
    public ShareBannerView(final Context context)
    {
        super(context);
        inflate(getContext(), R.layout.share_banner_view, this);
        ButterKnife.bind(this);
        setClickable(true);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.handy_white));
    }
}
