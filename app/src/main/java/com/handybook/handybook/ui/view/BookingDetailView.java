package com.handybook.handybook.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.util.TextUtils;

import java.util.Calendar;
import java.util.Date;

import butterknife.InjectView;

public final class BookingDetailView extends InjectedRelativeLayout
{
    @InjectView(R.id.date_text)
    TextView dateText;
    @InjectView(R.id.time_text)
    TextView timeText;
    @InjectView(R.id.freq_text)
    TextView freqText;
    @InjectView(R.id.freq_layout)
    View freqLayout;
    @InjectView(R.id.options_layout)
    View optionsLayout;
    @InjectView(R.id.reschedule_button)
    public Button rescheduleButton;
    @InjectView(R.id.cancel_button)
    public Button cancelButton;
    @InjectView(R.id.booking_text)
    TextView bookingText;
    @InjectView(R.id.nav_text)
    TextView navText;

    public BookingDetailView(final Context context)
    {
        super(context);
    }

    public BookingDetailView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void updateDisplay(final Booking booking, final User user)
    {
        final ViewGroup container = this;

        navText.setText(booking.getService());
        bookingText.setText("Booking #" + booking.getId());

        updateDateTimeInfoText(booking);

        updateFrequencySectionDisplay(booking);

        if (booking.isPast())
        {
            optionsLayout.setVisibility(View.GONE);
        }
    }

    //TODO: don't like having an exception the fragment should talk to the view in as few ways as possible
    public void updateDateTimeInfoText(final Booking booking)
    {
        updateDateTimeInfoText(booking, booking.getStartDate());
    }

    public void updateDateTimeInfoText(final Booking booking, final Date startDate)
    {
        final float hours = booking.getHours();
        final Calendar endDate = Calendar.getInstance();
        endDate.setTime(startDate);
        endDate.add(Calendar.HOUR, (int) hours);

        timeText.setText(TextUtils.formatDate(startDate, "h:mmaaa - ")
                + TextUtils.formatDate(endDate.getTime(), "h:mmaaa (") + TextUtils.formatDecimal(hours, "#.#") + " "
                + getResources().getQuantityString(R.plurals.hour, (int) Math.ceil(hours)) + ")");

        dateText.setText(TextUtils.formatDate(startDate, "EEEE',' MMM d',' yyyy"));
    }

    private void updateFrequencySectionDisplay(final Booking booking)
    {
        final String recurringInfo = booking.getRecurringInfo();
        if (recurringInfo == null)
        {
            freqLayout.setVisibility(View.GONE);
        }
        else
        {
            freqText.setText(booking.getRecurringInfo());
        }
    }
}
