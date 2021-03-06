package com.handybook.handybook.booking.ui.view;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.util.BookingUtil;
import com.handybook.handybook.library.ui.view.InjectedRelativeLayout;
import com.handybook.handybook.library.util.DateTimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

// TODO: Continue chopping this class up into fragments so all the elements in BookingDetailFragment
// are BookingDetailSectionFragments
public final class BookingDetailView extends InjectedRelativeLayout {

    private static final String START_DATE_FORMAT_PATTERN = "EEEE',' MMM d',' yyyy";
    @BindView(R.id.date_text)
    TextView dateText;
    @BindView(R.id.time_text)
    TextView timeText;
    @BindView(R.id.freq_text)
    TextView freqText;
    @BindView(R.id.freq_layout)
    View freqLayout;
    @BindView(R.id.service_icon)
    ServiceIconImageView serviceIcon;
    @BindView(R.id.booking_text)
    TextView bookingText;
    @BindView(R.id.nav_text)
    TextView navText;
    @BindView(R.id.add_to_calendar_image)
    ImageView mAddToCalendarImage;
    @BindView(R.id.booking_report_issue_button)
    Button mReportIssueButton;
    @BindView(R.id.back_button)
    public ImageButton backButton;

    private static final String HANDY_BOOKING = "Handy Booking";
    private Context mContext;
    private Booking mBooking;

    public BookingDetailView(final Context context) {
        super(context);
        init(context);
    }

    public BookingDetailView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BookingDetailView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void updateDisplay(
            final Booking booking,
            List<Service> serviceList,
            boolean isBookingHoursClarificationExperimentEnabled
    ) {
        mBooking = booking;
        navText.setText(booking.getServiceName());
        bookingText.setText(getContext().getString(R.string.booking_number, booking.getId()));
        updateDateTimeInfoText(booking, isBookingHoursClarificationExperimentEnabled);
        updateFrequencySectionDisplay(booking);
        updateServiceIcon(booking, serviceList);
    }

    @OnClick(R.id.add_to_calendar_image)
    public void addToCalendarClicked() {
        if (mBooking != null && mBooking.getAddress() != null &&
            mBooking.getStartDate() != null && mBooking.getEndDate() != null) {
            try {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(
                        CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        mBooking.getStartDate().getTime()
                );
                intent.putExtra(
                        CalendarContract.EXTRA_EVENT_END_TIME,
                        mBooking.getEndDate().getTime()
                );
                intent.putExtra(CalendarContract.Events.TITLE, HANDY_BOOKING);
                mContext.startActivity(intent);
            }
            catch (ActivityNotFoundException e) {
                mAddToCalendarImage.setVisibility(GONE);
                Crashlytics.logException(e);
            }
        }
    }

    public void updateServiceIcon(final Booking booking, List<Service> serviceList) {
        if (booking != null && serviceList != null && !serviceList.isEmpty()) {
            serviceIcon.updateServiceIconByBooking(booking, serviceList);
        }
    }

    //TODO: don't like having an exception the fragment should talk to the view in as few ways as
    // possible, this view is going to be supplanted by new sub fragments
    public void updateDateTimeInfoText(
            final Booking booking,
            boolean isBookingHoursClarificationExperimentEnabled
    ) {
        //Set the start date
        dateText.setText(DateTimeUtils.formatDate(booking.getStartDate(),
                                                  START_DATE_FORMAT_PATTERN,
                                                  booking.getBookingTimezone()
        ));

        //we want to display the time using the booking location's time zone
        String startTimeDisplayString = BookingUtil.getFormattedStartTime(booking);

        if (booking.shouldHideEndTime()) {
            //should only display the start time
            timeText.setText(startTimeDisplayString);
        }
        else {
            final float hours = booking.getHours();
            //hours is a float may come back as something like 3.5, and can't add float hours to a calendar
            final int minutes = (int) (60 * hours);
            //in the format "3 hours"
            String numHoursDisplayString = BookingUtil.getNumHoursDisplayString(
                    hours,
                    getContext()
            );

            if (isBookingHoursClarificationExperimentEnabled) {
                //5:00 pm (up to 3 hours)
                timeText.setText(getResources().getString(
                        R.string.booking_details_hours_clarification_experiment_hours_formatted,
                        startTimeDisplayString,
                        numHoursDisplayString
                ));
            }
            else {
                timeText.setText(getResources().getString(
                        R.string.booking_details_hours_formatted,
                        startTimeDisplayString,
                        BookingUtil.getFormattedEndTime(booking),
                        numHoursDisplayString
                ));
            }
        }
    }

    public void updateReportIssueButton(
            final Booking booking,
            final OnClickListener onClickListener
    ) {
        mReportIssueButton.setVisibility(booking.isMilestonesEnabled() ? VISIBLE : GONE);
        mReportIssueButton.setOnClickListener(onClickListener);
    }

    private void init(Context context) {
        mContext = context;
    }

    private void updateFrequencySectionDisplay(final Booking booking) {
        final String recurringInfo = booking.getRecurringInfo();
        if (recurringInfo == null) {
            freqLayout.setVisibility(View.GONE);
        }
        else {
            freqText.setText(booking.getRecurringInfo());
        }
    }
}
