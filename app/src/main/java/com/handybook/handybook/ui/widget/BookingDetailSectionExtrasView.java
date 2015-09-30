package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by cdavis on 9/1/15.
 */
public class BookingDetailSectionExtrasView extends BookingDetailSectionView
{
    @Bind(R.id.extras_section)
    public LinearLayout extrasSection;

    public BookingDetailSectionExtrasView(final Context context)
    {
        super(context);
    }

    public BookingDetailSectionExtrasView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailSectionExtrasView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void updateExtrasDisplay(final Booking booking)
    {
        final ArrayList<Booking.ExtraInfo> extras = booking.getExtrasInfo();
        if (extras != null && extras.size() > 0)
        {
            for (int i = 0; i < extras.size(); i++)
            {
                final Booking.ExtraInfo info = extras.get(i);
                BookingDetailSectionExtrasEntryView extrasEntryView = (BookingDetailSectionExtrasEntryView) inflate(R.layout.layout_section_extras_entry, extrasSection);
                extrasEntryView.updateDisplay(info);
            }
        }
        else
        {
            setVisibility(View.GONE);
        }
    }

}
