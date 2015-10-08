package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BookingOption;

public abstract class BookingOptionsView extends FrameLayout
{
    protected BookingOption option;
    protected OnUpdatedListener updateListener;
    protected ViewGroup mainLayout;

    BookingOptionsView(final Context context, final int layout, final BookingOption option,
                       final OnUpdatedListener updateListener)
    {
        super(context);
        init(context, layout, option, updateListener);
    }

    BookingOptionsView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    BookingOptionsView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    private void init(final Context context, final int layout, final BookingOption option,
                      final OnUpdatedListener updateListener)
    {
        this.option = option;
        this.updateListener = updateListener;
        LayoutInflater.from(context).inflate(layout, this);
    }

    public static interface OnUpdatedListener
    {
        void onUpdate(BookingOptionsView view);

        void onShowChildren(BookingOptionsView view, String[] items);

        void onHideChildren(BookingOptionsView view, String[] items);
    }

    public final void hideSeparator()
    {
        final int paddingBottom = mainLayout.getPaddingBottom(),
                paddingLeft = mainLayout.getPaddingLeft(),
                paddingRight = mainLayout.getPaddingRight(),
                paddingTop = mainLayout.getPaddingTop();

        mainLayout.setBackgroundResource((R.drawable.cell_booking_last));
        mainLayout.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        invalidate();
        requestLayout();
    }

    public abstract String getCurrentValue();
}
