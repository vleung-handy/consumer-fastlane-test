package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.util.AttributeSet;

public class BookingDetailSectionBookingActionsView extends BookingDetailSectionView
{
    public BookingDetailSectionBookingActionsView(final Context context)
    {
        super(context);
    }

    public BookingDetailSectionBookingActionsView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailSectionBookingActionsView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init()
    {
        super.init();
        showSeparator(false);
    }
}
