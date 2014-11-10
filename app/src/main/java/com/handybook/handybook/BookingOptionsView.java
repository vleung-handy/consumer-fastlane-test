package com.handybook.handybook;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

abstract class BookingOptionsView extends FrameLayout {
    protected BookingOption option;
    protected OnUpdatedListener updateListener;

    BookingOptionsView(final Context context, final int layout, final BookingOption option,
                       final OnUpdatedListener updateListener) {
        super(context);
        init(context, layout, option, updateListener);
    }

    BookingOptionsView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    BookingOptionsView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(final Context context, final int layout, final BookingOption option,
                      final OnUpdatedListener updateListener) {
        this.option = option;
        this.updateListener = updateListener;
        LayoutInflater.from(context).inflate(layout, this);
    }

    static interface OnUpdatedListener {
        void onUpdate (BookingOptionsView view);
        void onShowChildren (BookingOptionsView view, String[] items);
        void onHideChildren (BookingOptionsView view, String[] items);
    }

    abstract String getCurrentValue();

    abstract void hideSeparator();
}
