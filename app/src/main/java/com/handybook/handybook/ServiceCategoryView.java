package com.handybook.handybook;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

final class ServiceCategoryView extends FrameLayout {
    private final TextView textView;

    ServiceCategoryView(final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_service_category, this);

        textView = (TextView)this.findViewById(R.id.text_view);
        textView.setTextColor(getResources().getColor(R.color.handy_blue));
    }

    final void setText(String text) {
        textView.setText(text);
        invalidate();
        requestLayout();
    }
}
