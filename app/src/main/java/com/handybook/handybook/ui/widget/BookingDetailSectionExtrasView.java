package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.ui.view.InjectedRelativeLayout;

import java.util.ArrayList;

import butterknife.InjectView;

/**
 * Created by cdavis on 9/1/15.
 */
public class BookingDetailSectionExtrasView extends InjectedRelativeLayout
{
    @InjectView(R.id.entry_title)
    public TextView entryTitle;
    @InjectView(R.id.entry_action_text)
    public TextView entryActionText;

    @InjectView(R.id.extras_section)
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

    //TODO: Clean this up
    public void updateExtrasDisplay(final Booking booking)
    {
        entryTitle.setText(R.string.extras);

        entryActionText.setText(R.string.edit);
        entryActionText.setVisibility(GONE); //Not current supporting editing extras, no endpoint to hit

        final ArrayList<Booking.ExtraInfo> extras = booking.getExtrasInfo();
        if (extras != null && extras.size() > 0)
        {
            for (int i = 0; i < extras.size(); i++)
            {
                final Booking.ExtraInfo info = extras.get(i);
                BookingDetailSectionExtrasEntryView extrasEntryView = (BookingDetailSectionExtrasEntryView) inflate(R.layout.element_booking_section_extras_entry, extrasSection);
                extrasEntryView.updateDisplay(info);
            }
        }
        else
        {
            setVisibility(View.GONE);
        }
    }

}
