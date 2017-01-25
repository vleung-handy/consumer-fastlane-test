package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.handybook.handybook.R;

import butterknife.ButterKnife;

public class BookingDetailSectionPreferencesView extends BookingDetailSectionView
{
    public BookingDetailSectionPreferencesView(final Context context)
    {
        super(context);
    }

    public BookingDetailSectionPreferencesView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailSectionPreferencesView(
            final Context context,
            final AttributeSet attrs,
            final int defStyle
    )
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init()
    {
        inflate(getContext(), R.layout.view_booking_detail_section_preferences, this);
        ButterKnife.bind(this);
    }


}
