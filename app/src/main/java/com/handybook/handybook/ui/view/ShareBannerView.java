package com.handybook.handybook.ui.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.handybook.handybook.R;

import butterknife.ButterKnife;

public class ShareBannerView extends FrameLayout
{
    public ShareBannerView(final Context context)
    {
        super(context);
        inflate(getContext(), R.layout.share_banner_view, this);
        ButterKnife.bind(this);
        setClickable(true);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.handy_white));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin_half);
        layoutParams.setMargins(0, margin, 0, margin);
        setLayoutParams(layoutParams);
    }
}
