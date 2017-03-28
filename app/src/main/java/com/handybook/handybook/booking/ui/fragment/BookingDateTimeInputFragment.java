package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.fragment.dialog.BookingTimeInputDialogFragment;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.model.response.ProAvailabilityResponse;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.FragmentUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.handybook.handybook.library.util.DateTimeUtils.YEAR_MONTH_DATE_FORMATTER;

/**
 * this is a fragment instead of a custom view because:
 * <p>
 * - need to init and launch dialog fragment to capture time input
 * and want to encapsulate that logic so that this input can be easily moved
 * - eventually this will be needed in booking flow consolidation experiments
 * so more logic from BookingDateFragment may be moved here. may want to inject things
 */

public class BookingDateTimeInputFragment extends InjectedFragment
        implements BookingTimeInputDialogFragment.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    private SimpleDateFormat mEditDateButtonFormatter;
    private DateFormat mEditTimeButtonFormatter;
    @Bind(R.id.booking_edit_date_button)
    Button mBookingEditDateButton;
    @Bind(R.id.booking_edit_time_button)
    Button mBookingEditTimeButton;
    private DatePickerDialog mDatePickerDialog;
    private ProAvailabilityResponse mProAvailability;
    private Calendar mSelectedDateTime;

    public static final String BUNDLE_KEY_START_DATE_TIME = "BUNDLE_KEY_START_DATE_TIME";
    public static final String BUNDLE_KEY_DATE_DISPLAY_PATTERN = "BUNDLE_KEY_DATE_DISPLAY_PATTERN";

    public static BookingDateTimeInputFragment newInstance(
            @NonNull Calendar startDateTimeWithTimezone,
            @NonNull String dateDisplayPattern
    ) {
        return newInstance(startDateTimeWithTimezone, dateDisplayPattern, null);
    }

    public static BookingDateTimeInputFragment newInstance(
            @NonNull Calendar startDateTimeWithTimezone,
            @NonNull String dateDisplayPattern,
            @Nullable ProAvailabilityResponse availabilityResponse
    ) {
        BookingDateTimeInputFragment fragment = new BookingDateTimeInputFragment();
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_KEY_START_DATE_TIME, startDateTimeWithTimezone);
        args.putString(BUNDLE_KEY_DATE_DISPLAY_PATTERN, dateDisplayPattern);
        args.putSerializable(BundleKeys.PRO_AVAILABILITY, availabilityResponse);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        final View view = getActivity().getLayoutInflater()
                                       .inflate(
                                               R.layout.fragment_booking_date_time_input,
                                               container,
                                               false
                                       );

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        Calendar startDateAndTime = (Calendar) args.getSerializable(BUNDLE_KEY_START_DATE_TIME);
        String dateDisplayPattern = args.getString(BUNDLE_KEY_DATE_DISPLAY_PATTERN);
        mProAvailability = (ProAvailabilityResponse)
                args.getSerializable(BundleKeys.PRO_AVAILABILITY);
        mEditTimeButtonFormatter = android.text.format.DateFormat.getTimeFormat(getContext());
        mEditTimeButtonFormatter.setTimeZone(startDateAndTime.getTimeZone());
        /*
        get the device default time display format (ex. 1:00 pm, 13:00)
        ASSUMING the format doesn't include seconds, milliseconds..
         */

        mEditDateButtonFormatter =
                new SimpleDateFormat(dateDisplayPattern, Locale.getDefault());
        mEditDateButtonFormatter.setTimeZone(startDateAndTime.getTimeZone());

        mSelectedDateTime = startDateAndTime;

        initDatePicker();
        updateDateTimeDisplay();
    }

    private void initDatePicker() {
        mDatePickerDialog = DatePickerDialog.newInstance(
                this,
                mSelectedDateTime.get(Calendar.YEAR),
                mSelectedDateTime.get(Calendar.MONTH),
                mSelectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        mDatePickerDialog.setAccentColor(ContextCompat.getColor(getContext(), R.color.handy_blue));

        final Calendar day = Calendar.getInstance();
        day.setTimeZone(mSelectedDateTime.getTimeZone());
        // show the following year or just pro available dates
        if (mProAvailability == null) {
            //set min/max dates
            day.setTime(new Date());
            mDatePickerDialog.setMinDate(day);

            // set max date to one year from today
            day.set(Calendar.YEAR, day.get(Calendar.YEAR) + 1);
            day.set(Calendar.DATE, day.get(Calendar.DATE) - 1);
            mDatePickerDialog.setMaxDate(day);
        }
        else {
            List<ProAvailabilityResponse.Availability> availabilities
                    = mProAvailability.getAvailabilities();
            List<Calendar> availableDays = new ArrayList<>();

            for (ProAvailabilityResponse.Availability availability : availabilities) {
                try {
                    Date date = YEAR_MONTH_DATE_FORMATTER.parse(availability.getDate());
                    day.setTime(date);
                    availableDays.add((Calendar) day.clone());
                }
                catch (ParseException e) { }
            }
            mDatePickerDialog.setSelectableDays(availableDays.toArray(new Calendar[availableDays.size()]));
        }
    }

    @OnClick(R.id.booking_edit_time_button)
    public void onEditTimeButtonClicked() {
        if (getChildFragmentManager().findFragmentByTag(BookingTimeInputDialogFragment.TAG) ==
            null) {
            int hourOfDay = mSelectedDateTime.get(Calendar.HOUR_OF_DAY);
            int minuteOfHour = mSelectedDateTime.get(Calendar.MINUTE);

            int minutesOfDay = (int) (TimeUnit.HOURS.toMinutes(hourOfDay) + minuteOfHour);
            BookingTimeInputDialogFragment bookingTimeInputDialogFragment
                    = BookingTimeInputDialogFragment.newInstance(
                    minutesOfDay,
                    mEditTimeButtonFormatter
            );
            FragmentUtils.safeLaunchDialogFragment(
                    bookingTimeInputDialogFragment,
                    BookingDateTimeInputFragment.this,
                    BookingTimeInputDialogFragment.TAG
            );
        }
    }

    @OnClick(R.id.booking_edit_date_button)
    public void onEditDateButtonClicked() {
        //show the date picker dialog
        mDatePickerDialog.show(getActivity().getFragmentManager(), "");
    }

    public void updateDateTimeDisplay() {
        Date selectedDate = mSelectedDateTime.getTime();

        mBookingEditDateButton.setText(
                mEditDateButtonFormatter.format(selectedDate));
        mBookingEditTimeButton.setText(
                mEditTimeButtonFormatter.format(selectedDate).toLowerCase());
        //SimpleDateFormat apparently doesn't allow us to specify lowercase am/pm
    }

    private void notifyTimeUpdatedListener() {
        ((OnSelectedDateTimeUpdatedListener) getParentFragment()).onSelectedDateTimeUpdatedListener(
                mSelectedDateTime);
    }

    @Override
    public void OnTimeSet(int hourOfDay, int minuteOfHour) {
        mSelectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mSelectedDateTime.set(Calendar.MINUTE, minuteOfHour);
        updateDateTimeDisplay();
        notifyTimeUpdatedListener();
    }

    @Override
    public void onDateSet(
            final DatePickerDialog view,
            final int year,
            final int monthOfYear,
            final int dayOfMonth
    ) {
        mSelectedDateTime.set(Calendar.YEAR, year);
        mSelectedDateTime.set(Calendar.MONTH, monthOfYear);
        mSelectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateDateTimeDisplay();
        notifyTimeUpdatedListener();
    }

    public interface OnSelectedDateTimeUpdatedListener {

        void onSelectedDateTimeUpdatedListener(Calendar selectedDateTime);
    }
}
