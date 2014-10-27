package com.handybook.handybook;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

final class ServiceCategoryView extends FrameLayout {
    private TextView textView;

    ServiceCategoryView(final Context context) {
        super(context);
        init(context);
    }

    ServiceCategoryView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    ServiceCategoryView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(final Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_service_category, this);
        textView = (TextView)this.findViewById(R.id.text_view);
    }

    final void setText(final String text) {
        textView.setText(text);
        invalidate();
        requestLayout();
    }
}
