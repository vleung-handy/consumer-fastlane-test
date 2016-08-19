package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.ui.view.InjectedRelativeLayout;
import com.handybook.handybook.util.DateTimeUtils;
import com.handybook.handybook.util.TextUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    @Bind(R.id.booking_report_issue_button)
    Button mReportIssueButton;
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

    public void updateDisplay(final Booking booking, List<Service> serviceList)
    {
        navText.setText(booking.getServiceName());
        bookingText.setText(getContext().getString(R.string.booking_number) + booking.getId());
        updateDateTimeInfoText(booking);
        updateFrequencySectionDisplay(booking);
        updateServiceIcon(booking, serviceList);
    }

    public void updateServiceIcon(final Booking booking, List<Service> serviceList)
    {
        if (booking != null && serviceList != null && !serviceList.isEmpty())
        {
            serviceIcon.updateServiceIconByBooking(booking, serviceList);
        }
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

        //we want to display the time using the booking location's time zone
        timeText.setText(DateTimeUtils.formatDate(startDate, "h:mm aaa – ", booking.getBookingTimezone())
                + DateTimeUtils.formatDate(endDate.getTime(), "h:mm aaa (", booking.getBookingTimezone())
                + TextUtils.formatDecimal(hours, "#.#") + " "
                + getResources().getQuantityString(R.plurals.hour, (int) Math.ceil(hours)) + ")");

        dateText.setText(DateTimeUtils.formatDate(startDate, "EEEE',' MMM d',' yyyy",
                booking.getBookingTimezone()));
    }

    public void updateReportIssueButton(final Booking booking, final OnClickListener onClickListener)
    {
        mReportIssueButton.setVisibility(booking.isMilestonesEnabled() ? VISIBLE : GONE);
        mReportIssueButton.setOnClickListener(onClickListener);
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
