package com.handybook.handybook.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.widget.ServiceIconImageView;
import com.handybook.handybook.util.TextUtils;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;

// TODO: Continue chopping this class up into fragments so all the elements in BookingDetailFragment
// are BookingDetailSectionFragments

public final class BookingDetailView extends InjectedRelativeLayout
{
    @Bind(R.id.date_text)
    TextView dateText;
    @Bind(R.id.time_text)
    TextView timeText;
    @Bind(R.id.freq_text)
    TextView freqText;
    @Bind(R.id.freq_layout)
    View freqLayout;
    @Bind(R.id.service_icon)
    ServiceIconImageView serviceIcon;
    @Bind(R.id.booking_text)
    TextView bookingText;
    @Bind(R.id.nav_text)
    TextView navText;
    @Bind(R.id.section_fragment_container)
    public LinearLayout sectionFragmentContainer;
    @Bind(R.id.back_button)
    public ImageButton backButton;

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
        navText.setText(booking.getServiceName());
        bookingText.setText(getContext().getString(R.string.booking_number) + booking.getId());

        updateDateTimeInfoText(booking);

        updateFrequencySectionDisplay(booking);

        serviceIcon.updateServiceIconByBooking(booking);
    }

    //TODO: don't like having an exception the fragment should talk to the view in as few ways as
    // possible, this view is going to be supplanted by new sub fragments
    public void updateDateTimeInfoText(final Booking booking)
    {
        updateDateTimeInfoText(booking, booking.getStartDate());
    }

    public void updateDateTimeInfoText(final Booking booking, final Date startDate)
    {
        final float hours = booking.getHours();
        //hours is a float may come back as something like 3.5, and can't add float hours to a calendar
        final int minutes = (int) (60 * hours);
        final Calendar endDate = Calendar.getInstance();

        endDate.setTime(startDate);
        endDate.add(Calendar.MINUTE, minutes);

        timeText.setText(TextUtils.formatDate(startDate, "h:mm aaa - ")
                + TextUtils.formatDate(endDate.getTime(), "h:mm aaa (")
                + TextUtils.formatDecimal(hours, "#.#") + " "
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
